package com.finmate.domain.service;

import com.finmate.domain.model.DecisionSession;
import com.finmate.domain.port.DecisionSessionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Guides the user through a structured decision-making process before a financial decision.
 * Inspired by Thinking Fast and Slow (Kahneman) — acts as a cognitive brake.
 */
@ApplicationScoped
public class DecisionCoachingService {

    private static final Logger LOG = Logger.getLogger(DecisionCoachingService.class.getName());

    private static final String CLAUDE_API_URL      = "https://api.anthropic.com/v1/messages";
    private static final String OPENAI_API_URL      = "https://api.openai.com/v1/chat/completions";
    private static final String HUGGINGFACE_API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String CLAUDE_MODEL        = "claude-opus-4-6";
    private static final String OPENAI_MODEL        = "gpt-4o";
    private static final int    MAX_TOKENS          = 1024;

    private static final String SYSTEM_PROMPT =
            "Tu es FinMate, un assistant financier éducatif inspiré par Daniel Kahneman (Thinking Fast and Slow). " +
            "Ton rôle est d'agir comme un frein cognitif avant une décision financière importante. " +
            "Analyse les réponses de l'utilisateur et génère une recommandation structurée qui : " +
            "1) Valide les aspects positifs de sa réflexion, " +
            "2) Identifie les points de vigilance ou incohérences, " +
            "3) Donne une recommandation concrète et équilibrée. " +
            "Encourage la réflexion rationnelle et la patience. " +
            "Ne fais jamais de recommandations sur des titres spécifiques. " +
            "Réponds en français, de façon bienveillante mais honnête, en 4-6 paragraphes.";

    private final DecisionSessionRepository sessionRepository;
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

    public DecisionCoachingService(DecisionSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
        this.httpClient = HttpClient.newHttpClient();
    }

    public record DecisionCoachingResult(
            UUID sessionId,
            String recommendation,
            String decisionContext,
            String whyInvesting,
            String investmentHorizon,
            String riskTolerance,
            String financialGoal
    ) {}

    public DecisionCoachingResult coach(UUID userId, String decisionContext, String whyInvesting,
                                        String investmentHorizon, String riskTolerance, String financialGoal) {
        String prompt = buildPrompt(decisionContext, whyInvesting, investmentHorizon, riskTolerance, financialGoal);
        String recommendation = callLLM(prompt);

        DecisionSession session = new DecisionSession();
        session.setUserId(userId);
        session.setDecisionContext(decisionContext);
        session.setWhyInvesting(whyInvesting);
        session.setInvestmentHorizon(investmentHorizon);
        session.setRiskTolerance(riskTolerance);
        session.setFinancialGoal(financialGoal);
        session.setRecommendation(recommendation);
        session.setCreatedAt(LocalDateTime.now());
        DecisionSession saved = sessionRepository.save(session);

        return new DecisionCoachingResult(saved.getId(), recommendation, decisionContext,
                whyInvesting, investmentHorizon, riskTolerance, financialGoal);
    }

    // ── Prompt builder ────────────────────────────────────────────────────────

    private String buildPrompt(String context, String why, String horizon, String riskTolerance, String goal) {
        return String.format(
                "Je souhaite prendre une décision financière importante et j'aimerais ton avis réfléchi.%n%n" +
                "Ma décision : %s%n%n" +
                "Pourquoi je veux investir : %s%n%n" +
                "Mon horizon d'investissement : %s%n%n" +
                "Ma tolérance au risque : %s%n%n" +
                "Mon objectif financier : %s%n%n" +
                "Analyse ma situation et guide-moi vers une décision rationnelle.",
                context, why, horizon, riskTolerance, goal);
    }

    // ── LLM dispatcher ───────────────────────────────────────────────────────

    private String callLLM(String userMessage) {
        return switch (aiProvider.toLowerCase()) {
            case "openai"      -> callOpenAI(userMessage);
            case "huggingface" -> callHuggingFace(userMessage);
            default            -> callClaude(userMessage);
        };
    }

    private String callClaude(String userMessage) {
        String body = "{\"model\":\"" + CLAUDE_MODEL + "\",\"max_tokens\":" + MAX_TOKENS
                + ",\"system\":\"" + escapeJson(SYSTEM_PROMPT) + "\""
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

    private String callOpenAI(String userMessage) {
        String msgs = "[{\"role\":\"system\",\"content\":\"" + escapeJson(SYSTEM_PROMPT) + "\""
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

    private String callHuggingFace(String userMessage) {
        String msgs = "[{\"role\":\"system\",\"content\":\"" + escapeJson(SYSTEM_PROMPT) + "\""
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
            LOG.warning("HuggingFace decision-coaching status=" + resp.statusCode());
            if (resp.statusCode() != 200)
                throw new RuntimeException("Erreur HuggingFace API " + resp.statusCode() + " : " + resp.body());
            return extractJsonString(resp.body(), "\"content\":");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Impossible de contacter l'assistant : " + e.getMessage(), e);
        }
    }

    // ── JSON helpers ──────────────────────────────────────────────────────────

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
