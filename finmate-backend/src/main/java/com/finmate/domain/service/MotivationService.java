package com.finmate.domain.service;

import com.finmate.domain.model.Goal;
import com.finmate.domain.port.GoalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Generates personalized motivational messages based on the user's financial progress.
 * Uses saving streak, financial score, and goal progression data.
 */
@ApplicationScoped
public class MotivationService {

    private static final Logger LOG = Logger.getLogger(MotivationService.class.getName());

    private static final String CLAUDE_API_URL      = "https://api.anthropic.com/v1/messages";
    private static final String OPENAI_API_URL      = "https://api.openai.com/v1/chat/completions";
    private static final String HUGGINGFACE_API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String CLAUDE_MODEL        = "claude-opus-4-6";
    private static final String OPENAI_MODEL        = "gpt-4o";
    private static final int    MAX_TOKENS          = 600;

    private static final String SYSTEM_PROMPT =
            "Tu es FinMate, un coach financier bienveillant et motivant pour jeunes investisseurs. " +
            "Tu analyses les progrès financiers de l'utilisateur et génères un message d'encouragement personnalisé. " +
            "Ton message doit : " +
            "1) Valoriser les efforts réels de l'utilisateur (même petits), " +
            "2) Rappeler l'importance de la régularité et du long terme, " +
            "3) Donner un encouragement concret pour continuer. " +
            "Sois chaleureux, sincère et motivant — jamais condescendant. " +
            "Si les résultats sont faibles, encourage sans juger. " +
            "Réponds en français, en 3-4 paragraphes concis.";

    private final SavingStreakService savingStreakService;
    private final FinancialScoreService financialScoreService;
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

    public MotivationService(SavingStreakService savingStreakService,
                             FinancialScoreService financialScoreService,
                             GoalRepository goalRepository,
                             GoalProgressService goalProgressService) {
        this.savingStreakService  = savingStreakService;
        this.financialScoreService = financialScoreService;
        this.goalRepository       = goalRepository;
        this.goalProgressService  = goalProgressService;
        this.httpClient           = HttpClient.newHttpClient();
    }

    public record MotivationStats(
            int currentStreak,
            int longestStreak,
            int financialScore,
            String scoreLabel,
            int averageGoalProgress,
            int activeGoals
    ) {}

    public record MotivationResult(
            String message,
            MotivationStats stats
    ) {}

    public MotivationResult motivate(UUID userId) {
        var streak = savingStreakService.computeAndSave(userId);
        var score  = financialScoreService.compute(userId);
        var goals  = goalRepository.findByUserId(userId);

        int avgGoalProgress = computeAverageGoalProgress(goals, userId);

        MotivationStats stats = new MotivationStats(
                streak.getCurrentStreak(),
                streak.getLongestStreak(),
                score.score(),
                scoreLabelFr(score.label()),
                avgGoalProgress,
                goals.size()
        );

        String prompt   = buildPrompt(stats);
        String message  = callLLM(prompt);
        return new MotivationResult(message, stats);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private int computeAverageGoalProgress(List<Goal> goals, UUID userId) {
        if (goals.isEmpty()) return 0;
        int total = 0;
        for (Goal goal : goals) {
            try {
                var progress = goalProgressService.getProgress(goal.getId(), userId);
                total += progress.percent();
            } catch (Exception ignored) {}
        }
        return total / goals.size();
    }

    private String scoreLabelFr(String label) {
        return switch (label) {
            case "healthy"   -> "Solide";
            case "improving" -> "En progression";
            default          -> "À améliorer";
        };
    }

    // ── Prompt builder ────────────────────────────────────────────────────────

    private String buildPrompt(MotivationStats stats) {
        StringBuilder sb = new StringBuilder("Voici les progrès financiers de l'utilisateur :\n\n");
        sb.append(String.format("Streak d'épargne : %d mois consécutifs (record : %d mois)%n",
                stats.currentStreak(), stats.longestStreak()));
        sb.append(String.format("Score financier : %d/100 (%s)%n",
                stats.financialScore(), stats.scoreLabel()));
        sb.append(String.format("Objectifs actifs : %d%n", stats.activeGoals()));
        if (stats.activeGoals() > 0) {
            sb.append(String.format("Progression moyenne des objectifs : %d%%%n", stats.averageGoalProgress()));
        }
        sb.append("\nGénère un message d'encouragement personnalisé et motivant.");
        return sb.toString();
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
            LOG.warning("HuggingFace motivation status=" + resp.statusCode());
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
