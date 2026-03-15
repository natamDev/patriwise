package com.finmate.domain.service;

import com.finmate.domain.model.Goal;
import com.finmate.domain.port.GoalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class SavingsCoachingService {

    private static final Logger LOG = Logger.getLogger(SavingsCoachingService.class.getName());

    private static final String CLAUDE_API_URL      = "https://api.anthropic.com/v1/messages";
    private static final String OPENAI_API_URL      = "https://api.openai.com/v1/chat/completions";
    private static final String HUGGINGFACE_API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String CLAUDE_MODEL        = "claude-opus-4-6";
    private static final String OPENAI_MODEL        = "gpt-4o";
    private static final int    MAX_TOKENS          = 1024;

    private static final String SYSTEM_PROMPT =
            "Tu es FinMate, un assistant financier éducatif spécialisé dans le coaching d'épargne. " +
            "Analyse les progrès des objectifs d'épargne de l'utilisateur et génère des conseils personnalisés. " +
            "Pour chaque objectif : évalue la progression, estime l'atteinte, propose des optimisations. " +
            "Encourage la discipline d'épargne et le DCA (Dollar Cost Averaging). " +
            "Ne fais jamais de recommandations sur des titres ou actions spécifiques. " +
            "Réponds en français, de façon concise (4-6 paragraphes), bienveillante et motivante.";

    private final GoalRepository goalRepository;
    private final GoalProgressService goalProgressService;
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

    public SavingsCoachingService(GoalRepository goalRepository, GoalProgressService goalProgressService) {
        this.goalRepository      = goalRepository;
        this.goalProgressService = goalProgressService;
        this.httpClient          = HttpClient.newHttpClient();
    }

    public record GoalCoachingItem(
            UUID goalId,
            String goalName,
            String goalType,
            double targetAmount,
            double savedAmount,
            double remainingAmount,
            int percent,
            Integer monthsNeeded,
            LocalDate estimatedCompletionDate,
            double monthlyContribution,
            Double optimizedContribution
    ) {}

    public record SavingsCoachingResult(
            String coaching,
            List<GoalCoachingItem> goals
    ) {}

    public SavingsCoachingResult coach(UUID userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);

        if (goals.isEmpty()) {
            return new SavingsCoachingResult(
                    "Tu n'as pas encore créé d'objectifs d'épargne. " +
                    "Commence par définir un objectif dans l'onglet Objectifs : " +
                    "fonds d'urgence, projet de voyage, investissement... " +
                    "Même un petit objectif avec une contribution mensuelle régulière te mettra sur la bonne voie.",
                    List.of());
        }

        List<GoalCoachingItem> items = new ArrayList<>();
        for (Goal goal : goals) {
            GoalProgressService.GoalProgress progress = goalProgressService.getProgress(goal.getId(), userId);
            Double optimized = computeOptimizedContribution(goal, progress);
            items.add(new GoalCoachingItem(
                    goal.getId(),
                    goal.getGoalName(),
                    goal.getGoalType().name(),
                    progress.targetAmount().doubleValue(),
                    progress.savedAmount().doubleValue(),
                    progress.remainingAmount().doubleValue(),
                    progress.percent(),
                    progress.monthsNeeded(),
                    progress.estimatedCompletionDate(),
                    goal.getMonthlyContribution() != null ? goal.getMonthlyContribution().doubleValue() : 0,
                    optimized
            ));
        }

        String prompt  = buildPrompt(items);
        String coaching = callLLM(prompt);
        return new SavingsCoachingResult(coaching, items);
    }

    // ── Optimized contribution calculation ───────────────────────────────────

    /**
     * If the goal has a target date and the current contribution won't reach it in time,
     * compute the monthly contribution needed to hit the target date.
     * Returns null if the current contribution is sufficient or no optimization is needed.
     */
    private Double computeOptimizedContribution(Goal goal, GoalProgressService.GoalProgress progress) {
        if (progress.remainingAmount().compareTo(BigDecimal.ZERO) <= 0) return null; // already reached
        if (goal.getTargetDate() == null) return null;

        long monthsLeft = ChronoUnit.MONTHS.between(LocalDate.now(), goal.getTargetDate());
        if (monthsLeft <= 0) return null;

        double needed = progress.remainingAmount().divide(BigDecimal.valueOf(monthsLeft), 2, RoundingMode.CEILING).doubleValue();
        double current = goal.getMonthlyContribution() != null ? goal.getMonthlyContribution().doubleValue() : 0;

        // Only suggest optimization if needed is significantly higher than current (>10%)
        if (needed > current * 1.10) {
            return Math.ceil(needed);
        }
        return null;
    }

    // ── Prompt builder ────────────────────────────────────────────────────────

    private String buildPrompt(List<GoalCoachingItem> items) {
        StringBuilder sb = new StringBuilder("Analyse mes objectifs d'épargne et donne-moi un coaching personnalisé :\n\n");

        for (GoalCoachingItem item : items) {
            sb.append(String.format("Objectif : %s (%s)%n", item.goalName(), goalTypeLabel(item.goalType())));
            sb.append(String.format("  Progression : %d%% — %.0f € épargnés sur %.0f € visés (reste %.0f €)%n",
                    item.percent(), item.savedAmount(), item.targetAmount(), item.remainingAmount()));
            sb.append(String.format("  Contribution mensuelle actuelle : %.0f €%n", item.monthlyContribution()));
            if (item.monthsNeeded() != null) {
                sb.append(String.format("  Temps estimé pour atteindre l'objectif : %d mois%n", item.monthsNeeded()));
                if (item.estimatedCompletionDate() != null) {
                    sb.append(String.format("  Date d'atteinte estimée : %s%n", item.estimatedCompletionDate()));
                }
            }
            if (item.optimizedContribution() != null) {
                sb.append(String.format("  Contribution recommandée pour atteindre la date cible : %.0f €/mois%n",
                        item.optimizedContribution()));
            }
            sb.append("\n");
        }

        sb.append("Génère un coaching d'épargne complet avec des conseils concrets pour chaque objectif.");
        return sb.toString();
    }

    private String goalTypeLabel(String type) {
        return switch (type) {
            case "TRAVEL"         -> "Voyage";
            case "EMERGENCY_FUND" -> "Fonds d'urgence";
            case "INVESTMENT"     -> "Investissement";
            case "PURCHASE"       -> "Achat";
            default               -> "Autre";
        };
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
            LOG.warning("HuggingFace savings-coaching status=" + resp.statusCode());
            if (resp.statusCode() != 200)
                throw new RuntimeException("Erreur HuggingFace API " + resp.statusCode() + " : " + resp.body());
            return extractJsonString(resp.body(), "\"content\":");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Impossible de contacter l'assistant : " + e.getMessage(), e);
        }
    }

    // ── JSON helpers ─────────────────────────────────────────────────────────

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
