package com.finmate.domain.service;

import com.finmate.domain.exception.ResourceNotFoundException;
import com.finmate.domain.exception.ValidationException;
import com.finmate.domain.model.FinancialProfile;
import com.finmate.domain.model.Goal;
import com.finmate.domain.model.GoalAssistantSession;
import com.finmate.domain.port.FinancialProfileRepository;
import com.finmate.domain.port.GoalAssistantSessionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@ApplicationScoped
public class GoalAssistantService {

    private static final Logger LOG = Logger.getLogger(GoalAssistantService.class.getName());

    private static final String CLAUDE_API_URL      = "https://api.anthropic.com/v1/messages";
    private static final String OPENAI_API_URL      = "https://api.openai.com/v1/chat/completions";
    private static final String HUGGINGFACE_API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String CLAUDE_MODEL        = "claude-opus-4-6";
    private static final String OPENAI_MODEL        = "gpt-4o";
    private static final int    MAX_TOKENS          = 1024;

    private static final String INTENT_SYSTEM_PROMPT =
            "Tu es FinMate, un assistant financier éducatif pour les 18-25 ans. " +
            "L'utilisateur exprime une intention financière en langage naturel. " +
            "Ton rôle est de poser 2 à 3 questions de clarification numérotées pour mieux comprendre son projet. " +
            "Questions possibles : budget estimé, date souhaitée, capacité d'épargne mensuelle, priorité de cet objectif. " +
            "Adapte tes questions au contexte financier de l'utilisateur. " +
            "Sois bienveillant, éducatif, et encourage la discipline d'épargne. " +
            "Réponds UNIQUEMENT avec les questions numérotées, sans introduction ni conclusion.";

    private static final String PROPOSE_SYSTEM_PROMPT_TEMPLATE =
            "Tu es FinMate, un assistant financier éducatif. " +
            "À partir de l'intention de l'utilisateur et de ses réponses aux questions de clarification, " +
            "génère une proposition d'objectif financier structuré. " +
            "IMPORTANT : la date d'aujourd'hui est %s. La targetDate DOIT être strictement dans le futur (après aujourd'hui). " +
            "Réponds UNIQUEMENT avec un bloc JSON valide (sans texte autour) avec ces champs : " +
            "\"goalName\" (string, nom clair et court), " +
            "\"goalType\" (string parmi TRAVEL, EMERGENCY_FUND, INVESTMENT, PURCHASE, OTHER), " +
            "\"targetAmount\" (number, montant cible en euros), " +
            "\"targetDate\" (string au format YYYY-MM-DD, DOIT être dans le futur), " +
            "\"monthlyContribution\" (number, contribution mensuelle recommandée), " +
            "\"feasibilityAssessment\" (string, évaluation de la faisabilité en 2-3 phrases), " +
            "\"feasibilityPercent\" (number entre 0 et 100, basé sur le ratio contribution/revenu). " +
            "Règle de faisabilité : si la contribution mensuelle dépasse 30%% du revenu, feasibilityPercent < 50. " +
            "Si elle représente 10-30%%, feasibilityPercent entre 50 et 80. Si < 10%%, feasibilityPercent > 80.";

    private final GoalAssistantSessionRepository sessionRepository;
    private final GoalService goalService;
    private final FinancialProfileRepository profileRepository;
    private final HttpClient httpClient;

    @ConfigProperty(name = "finmate.ai.provider", defaultValue = "claude")
    String aiProvider;

    @ConfigProperty(name = "finmate.claude.api-key", defaultValue = "changeme")
    String claudeApiKey;

    @ConfigProperty(name = "finmate.openai.api-key", defaultValue = "changeme")
    String openaiApiKey;

    @ConfigProperty(name = "finmate.huggingface.api-key", defaultValue = "changeme")
    String huggingfaceApiKey;

    @ConfigProperty(name = "finmate.huggingface.model", defaultValue = "meta-llama/Llama-3.1-8B-Instruct")
    String huggingfaceModel;

    public GoalAssistantService(GoalAssistantSessionRepository sessionRepository,
                                GoalService goalService,
                                FinancialProfileRepository profileRepository) {
        this.sessionRepository = sessionRepository;
        this.goalService = goalService;
        this.profileRepository = profileRepository;
        this.httpClient = HttpClient.newHttpClient();
    }

    // ── Step 1: Analyze intent ─────────────────────────────────────────────────

    public GoalAssistantSession analyzeIntent(UUID userId, String userIntent) {
        String profileContext = buildProfileContext(userId);
        String prompt = "Intention de l'utilisateur : \"" + userIntent + "\"\n\n" + profileContext;
        String questions = callLLM(INTENT_SYSTEM_PROMPT, prompt);

        GoalAssistantSession session = new GoalAssistantSession();
        session.setUserId(userId);
        session.setUserIntent(userIntent);
        session.setClarificationQuestions(questions);
        session.setStatus(GoalAssistantSession.Status.AWAITING_CLARIFICATION);
        session.setCreatedAt(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    // ── Step 2: Propose goal ───────────────────────────────────────────────────

    public GoalAssistantSession proposeGoal(UUID userId, UUID sessionId, String userAnswers) {
        GoalAssistantSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session introuvable."));
        if (!session.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Session introuvable.");
        }

        String profileContext = buildProfileContext(userId);
        String prompt = "Intention initiale : \"" + session.getUserIntent() + "\"\n\n" +
                "Questions posées :\n" + session.getClarificationQuestions() + "\n\n" +
                "Réponses de l'utilisateur :\n" + userAnswers + "\n\n" + profileContext;

        String proposePrompt = String.format(PROPOSE_SYSTEM_PROMPT_TEMPLATE, LocalDate.now());
        String llmResponse = callLLM(proposePrompt, prompt);
        String jsonBlock = extractJsonBlock(llmResponse);

        try (JsonReader reader = Json.createReader(new StringReader(jsonBlock))) {
            JsonObject json = reader.readObject();
            session.setProposedGoalName(json.getString("goalName", "Mon objectif"));
            session.setProposedGoalType(parseGoalType(json.getString("goalType", "OTHER")));
            session.setProposedTargetAmount(new BigDecimal(json.getJsonNumber("targetAmount").toString()));
            LocalDate proposedDate = LocalDate.parse(json.getString("targetDate"));
            if (!proposedDate.isAfter(LocalDate.now())) {
                proposedDate = LocalDate.now().plusMonths(6);
            }
            session.setProposedTargetDate(proposedDate);
            session.setProposedMonthlyContribution(new BigDecimal(json.getJsonNumber("monthlyContribution").toString()));
            session.setFeasibilityAssessment(json.getString("feasibilityAssessment", ""));
            session.setFeasibilityPercent(json.getInt("feasibilityPercent", 50));
        } catch (Exception e) {
            LOG.warning("Failed to parse LLM JSON response: " + e.getMessage());
            throw new RuntimeException("L'assistant n'a pas pu structurer la proposition. Réessayez.");
        }

        session.setUserAnswers(userAnswers);
        session.setStatus(GoalAssistantSession.Status.PROPOSAL_SENT);
        return sessionRepository.save(session);
    }

    // ── Step 3: Confirm goal ───────────────────────────────────────────────────

    public GoalAssistantSession confirmGoal(UUID userId, UUID sessionId,
                                             String goalName, String goalType,
                                             BigDecimal targetAmount, LocalDate targetDate,
                                             BigDecimal monthlyContribution) {
        GoalAssistantSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session introuvable."));
        if (!session.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Session introuvable.");
        }

        Goal goal = new Goal();
        goal.setGoalName(goalName);
        goal.setGoalType(Goal.GoalType.valueOf(goalType));
        goal.setTargetAmount(targetAmount);
        goal.setTargetDate(targetDate);
        goal.setMonthlyContribution(monthlyContribution);
        Goal created = goalService.create(goal, userId);

        session.setProposedGoalName(goalName);
        session.setProposedGoalType(parseGoalType(goalType));
        session.setProposedTargetAmount(targetAmount);
        session.setProposedTargetDate(targetDate);
        session.setProposedMonthlyContribution(monthlyContribution);
        session.setStatus(GoalAssistantSession.Status.CONFIRMED);
        session.setCreatedGoalId(created.getId());
        return sessionRepository.save(session);
    }

    // ── Profile context builder ────────────────────────────────────────────────

    private String buildProfileContext(UUID userId) {
        return profileRepository.findByUserId(userId)
                .map(p -> "Contexte financier : revenu mensuel " + p.getMonthlyIncome() + " " + p.getCurrency()
                        + ", statut " + p.getEmploymentStatus()
                        + ", âge " + p.getAge()
                        + ", expérience " + p.getFinancialExperienceLevel() + ".")
                .orElse("Profil financier non renseigné.");
    }

    private GoalAssistantSession.GoalType parseGoalType(String type) {
        try {
            return GoalAssistantSession.GoalType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return GoalAssistantSession.GoalType.OTHER;
        }
    }

    // ── JSON block extraction ──────────────────────────────────────────────────

    private String extractJsonBlock(String text) {
        int start = text.indexOf('{');
        if (start == -1) throw new RuntimeException("Pas de JSON trouvé dans la réponse.");
        int depth = 0;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return text.substring(start, i + 1);
            }
        }
        throw new RuntimeException("JSON incomplet dans la réponse.");
    }

    // ── LLM dispatcher ────────────────────────────────────────────────────────

    private String callLLM(String systemPrompt, String userMessage) {
        return switch (aiProvider.toLowerCase()) {
            case "openai"      -> callOpenAI(systemPrompt, userMessage);
            case "huggingface" -> callHuggingFace(systemPrompt, userMessage);
            default            -> callClaude(systemPrompt, userMessage);
        };
    }

    private String callClaude(String systemPrompt, String userMessage) {
        String body = "{\"model\":\"" + CLAUDE_MODEL + "\",\"max_tokens\":" + MAX_TOKENS
                + ",\"system\":\"" + escapeJson(systemPrompt) + "\""
                + ",\"messages\":[{\"role\":\"user\",\"content\":\"" + escapeJson(userMessage) + "\"}]}";
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(CLAUDE_API_URL))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", claudeApiKey)
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) throw new RuntimeException("Erreur Claude API : " + resp.statusCode());
            return extractJsonString(resp.body(), "\"text\":");
        } catch (Exception e) {
            throw new RuntimeException("Impossible de contacter l'assistant.", e);
        }
    }

    private String callOpenAI(String systemPrompt, String userMessage) {
        String msgs = "[{\"role\":\"system\",\"content\":\"" + escapeJson(systemPrompt) + "\""
                + "},{\"role\":\"user\",\"content\":\"" + escapeJson(userMessage) + "\"}]";
        String body = "{\"model\":\"" + OPENAI_MODEL + "\",\"max_tokens\":" + MAX_TOKENS + ",\"messages\":" + msgs + "}";
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openaiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) throw new RuntimeException("Erreur OpenAI API : " + resp.statusCode());
            return extractJsonString(resp.body(), "\"content\":");
        } catch (Exception e) {
            throw new RuntimeException("Impossible de contacter l'assistant.", e);
        }
    }

    private String callHuggingFace(String systemPrompt, String userMessage) {
        String msgs = "[{\"role\":\"system\",\"content\":\"" + escapeJson(systemPrompt) + "\""
                + "},{\"role\":\"user\",\"content\":\"" + escapeJson(userMessage) + "\"}]";
        String body = "{\"model\":\"" + huggingfaceModel + "\",\"max_tokens\":" + MAX_TOKENS + ",\"messages\":" + msgs + "}";
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(HUGGINGFACE_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + huggingfaceApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            LOG.warning("HuggingFace goal-assistant status=" + resp.statusCode());
            if (resp.statusCode() != 200)
                throw new RuntimeException("Erreur HuggingFace API " + resp.statusCode() + " : " + resp.body());
            return extractJsonString(resp.body(), "\"content\":");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Impossible de contacter l'assistant : " + e.getMessage(), e);
        }
    }

    // ── JSON helpers ───────────────────────────────────────────────────────────

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String extractJsonString(String json, String marker) {
        int idx = json.indexOf(marker);
        if (idx == -1) throw new RuntimeException("Réponse inattendue de l'assistant.");
        int start = json.indexOf("\"", idx + marker.length()) + 1;
        int end = findEndOfString(json, start);
        return json.substring(start, end)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\t", "\t");
    }

    private int findEndOfString(String json, int start) {
        int i = start;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\') { i += 2; }
            else if (c == '"') { return i; }
            else { i++; }
        }
        return i;
    }
}
