package com.finmate.domain.service;

import com.finmate.domain.model.Expense;
import com.finmate.domain.model.FinancialProfile;
import com.finmate.domain.model.Goal;
import com.finmate.domain.port.ExpenseRepository;
import com.finmate.domain.port.GoalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class FinancialAnalysisService {

    private static final Logger LOG = Logger.getLogger(FinancialAnalysisService.class.getName());

    private static final String CLAUDE_API_URL    = "https://api.anthropic.com/v1/messages";
    private static final String OPENAI_API_URL    = "https://api.openai.com/v1/chat/completions";
    private static final String HUGGINGFACE_API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String CLAUDE_MODEL      = "claude-opus-4-6";
    private static final String OPENAI_MODEL      = "gpt-4o";
    private static final int    MAX_TOKENS        = 1024;

    private static final String SYSTEM_PROMPT =
            "Tu es FinMate, un assistant financier éducatif pour jeunes investisseurs. " +
            "Analyse les données financières fournies et génère une analyse claire, précise et bienveillante. " +
            "Structure ta réponse en 3 parties : " +
            "1) Situation budgétaire globale (revenu vs dépenses), " +
            "2) Points d'attention sur les dépenses, " +
            "3) Capacité d'épargne et recommandations concrètes. " +
            "Utilise des chiffres précis (pourcentages, montants). " +
            "Réponds en français, de façon concise (4-6 paragraphes). " +
            "Ne fais jamais de recommandations sur des titres ou actions spécifiques.";

    private final FinancialProfileService profileService;
    private final BudgetService budgetService;
    private final ExpenseRepository expenseRepository;
    private final GoalRepository goalRepository;
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

    public FinancialAnalysisService(
            FinancialProfileService profileService,
            BudgetService budgetService,
            ExpenseRepository expenseRepository,
            GoalRepository goalRepository) {
        this.profileService    = profileService;
        this.budgetService     = budgetService;
        this.expenseRepository = expenseRepository;
        this.goalRepository    = goalRepository;
        this.httpClient        = HttpClient.newHttpClient();
    }

    public record FinancialAnalysisResult(
            String analysis,
            Map<String, Double> expenseByCategory,
            double savingCapacity,
            List<String> spendingAlerts
    ) {}

    public FinancialAnalysisResult analyze(UUID userId) {
        var profileOpt = profileService.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            return new FinancialAnalysisResult(
                    "Ton profil financier n'est pas encore renseigné. " +
                    "Complète-le dans l'onglet Profil pour recevoir une analyse personnalisée.",
                    Map.of(), 0, List.of());
        }

        var profile = profileOpt.get();
        var budget  = budgetService.computeSummary(userId, YearMonth.now());
        var goals   = goalRepository.findByUserId(userId);

        LocalDate now        = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd   = now.withDayOfMonth(now.lengthOfMonth());
        var expenses = expenseRepository.findByUserIdAndMonth(userId, monthStart, monthEnd);

        double income        = profile.getMonthlyIncome().doubleValue();
        double totalExpenses = budget.totalExpenses().doubleValue();
        double savingCapacity = income - totalExpenses;

        Map<String, Double> expenseByCategory = new LinkedHashMap<>();
        List<String> spendingAlerts = new ArrayList<>();

        for (Expense.ExpenseCategory cat : Expense.ExpenseCategory.values()) {
            double catTotal = expenses.stream()
                    .filter(e -> e.getCategory() == cat)
                    .mapToDouble(e -> e.getAmount().doubleValue())
                    .sum();
            if (catTotal > 0) {
                double pct = income > 0 ? Math.round(catTotal / income * 1000.0) / 10.0 : 0;
                expenseByCategory.put(cat.name(), pct);
                if (pct > 30) {
                    spendingAlerts.add(categoryLabel(cat) + " représente " + (int) pct + "% de ton revenu");
                }
            }
        }

        String userPrompt = buildPrompt(profile, income, totalExpenses, savingCapacity, budget.savingsRate(), expenseByCategory, goals);
        String analysis   = callLLM(userPrompt);

        return new FinancialAnalysisResult(analysis, expenseByCategory, savingCapacity, spendingAlerts);
    }

    // ── Prompt builder ────────────────────────────────────────────────────────

    private String buildPrompt(FinancialProfile profile, double income, double totalExpenses,
                               double savingCapacity, double savingsRate,
                               Map<String, Double> expenseByCategory, List<Goal> goals) {
        StringBuilder sb = new StringBuilder("Analyse ma situation financière :\n\n");
        sb.append(String.format("Revenu mensuel : %.0f %s%n", income, profile.getCurrency()));
        sb.append(String.format("Dépenses totales ce mois : %.0f %s (%.0f%% du revenu)%n",
                totalExpenses, profile.getCurrency(), income > 0 ? totalExpenses / income * 100 : 0));
        sb.append(String.format("Taux d'épargne : %.0f%%%n", savingsRate * 100));
        sb.append(String.format("Capacité d'épargne mensuelle : %.0f %s%n", savingCapacity, profile.getCurrency()));
        sb.append(String.format("Expérience financière : %s%n", profile.getFinancialExperienceLevel().name().toLowerCase()));
        sb.append(String.format("Âge : %d ans%n%n", profile.getAge()));

        if (!expenseByCategory.isEmpty()) {
            sb.append("Répartition des dépenses par catégorie :\n");
            expenseByCategory.forEach((cat, pct) ->
                    sb.append(String.format("- %s : %.1f%% du revenu%n", categoryLabel(cat), pct)));
        } else {
            sb.append("Aucune dépense enregistrée ce mois-ci.\n");
        }

        if (!goals.isEmpty()) {
            sb.append(String.format("%nObjectifs actifs : %d%n", goals.size()));
            goals.forEach(g -> sb.append(String.format(
                    "- %s (%s) : objectif %.0f %s, contribution mensuelle %.0f %s%n",
                    g.getGoalName(), g.getGoalType().name().toLowerCase(),
                    g.getTargetAmount().doubleValue(), profile.getCurrency(),
                    g.getMonthlyContribution() != null ? g.getMonthlyContribution().doubleValue() : 0,
                    profile.getCurrency())));
        }

        sb.append("\nGénère une analyse complète de ma situation financière.");
        return sb.toString();
    }

    private String categoryLabel(String cat) {
        return switch (cat) {
            case "HOUSING"       -> "Logement";
            case "TRANSPORT"     -> "Transport";
            case "FOOD"          -> "Alimentation";
            case "SUBSCRIPTIONS" -> "Abonnements";
            case "ENTERTAINMENT" -> "Loisirs";
            case "SHOPPING"      -> "Shopping";
            case "HEALTH"        -> "Santé";
            default              -> "Autres";
        };
    }

    private String categoryLabel(Expense.ExpenseCategory cat) {
        return categoryLabel(cat.name());
    }

    // ── LLM dispatcher ───────────────────────────────────────────────────────

    private String callLLM(String userMessage) {
        return switch (aiProvider.toLowerCase()) {
            case "openai"       -> callOpenAI(userMessage);
            case "huggingface"  -> callHuggingFace(userMessage);
            default             -> callClaude(userMessage);
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
            LOG.warning("HuggingFace analysis status=" + resp.statusCode() + " body=" + resp.body());
            if (resp.statusCode() != 200)
                throw new RuntimeException("Erreur HuggingFace API " + resp.statusCode() + " : " + resp.body());
            return extractJsonString(resp.body(), "\"content\":");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Impossible de contacter l'assistant : " + e.getMessage(), e);
        }
    }

    // ── JSON helpers (same as AssistantService) ───────────────────────────────

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
