package com.finmate.domain.service;

import com.finmate.domain.model.Goal;
import com.finmate.domain.port.GoalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Generates personalized financial projections using the user's actual data
 * (financial profile + goals) across 3 return scenarios.
 */
@ApplicationScoped
public class FinancialProjectionService {

    private static final Logger LOG = Logger.getLogger(FinancialProjectionService.class.getName());

    private static final String CLAUDE_API_URL      = "https://api.anthropic.com/v1/messages";
    private static final String OPENAI_API_URL      = "https://api.openai.com/v1/chat/completions";
    private static final String HUGGINGFACE_API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String CLAUDE_MODEL        = "claude-opus-4-6";
    private static final String OPENAI_MODEL        = "gpt-4o";
    private static final int    MAX_TOKENS          = 1024;
    private static final int    DEFAULT_HORIZON_YEARS = 20;

    private static final double CONSERVATIVE_RETURN = 5.0;
    private static final double MODERATE_RETURN     = 7.0;
    private static final double OPTIMISTIC_RETURN   = 9.0;

    private static final String SYSTEM_PROMPT =
            "Tu es FinMate, un assistant financier éducatif. " +
            "L'utilisateur vient de consulter ses projections financières personnalisées. " +
            "Explique les résultats de façon claire, motivante et honnête. " +
            "Mets en valeur l'importance du temps et de la régularité (DCA). " +
            "Présente les 3 scénarios (conservateur, modéré, optimiste) sans promettre de rendements spécifiques. " +
            "Rappelle que les rendements passés ne garantissent pas les rendements futurs. " +
            "Donne un conseil concret pour améliorer la projection. " +
            "Réponds en français, en 4-5 paragraphes concis.";

    private final FinancialProfileService profileService;
    private final BudgetService budgetService;
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

    public FinancialProjectionService(FinancialProfileService profileService,
                                      BudgetService budgetService,
                                      GoalRepository goalRepository) {
        this.profileService = profileService;
        this.budgetService  = budgetService;
        this.goalRepository = goalRepository;
        this.httpClient     = HttpClient.newHttpClient();
    }

    public record Scenario(String label, double returnPct, double capitalInvested, double capitalFinal, double interestGain) {}

    public record ProjectionInput(double monthlyInvestment, int horizonYears) {}

    public record FinancialProjectionResult(
            double monthlyInvestment,
            int horizonYears,
            String currency,
            Scenario conservative,
            Scenario moderate,
            Scenario optimistic,
            String explanation
    ) {}

    public FinancialProjectionResult project(UUID userId) {
        var profileOpt = profileService.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            return new FinancialProjectionResult(0, DEFAULT_HORIZON_YEARS, "EUR",
                    emptyScenario("Conservateur"), emptyScenario("Modéré"), emptyScenario("Optimiste"),
                    "Ton profil financier n'est pas encore renseigné. Complète-le pour obtenir une projection personnalisée.");
        }

        var profile = profileOpt.get();
        BudgetService.BudgetSummary budget = budgetService.computeSummary(userId, YearMonth.now());
        var goals = goalRepository.findByUserId(userId);

        double monthlyInvestment = computeMonthlyInvestment(budget, goals);
        int horizonYears = computeHorizon(goals);
        String currency = profile.getCurrency();

        Scenario conservative = computeScenario("Conservateur", CONSERVATIVE_RETURN, monthlyInvestment, horizonYears);
        Scenario moderate     = computeScenario("Modéré",       MODERATE_RETURN,     monthlyInvestment, horizonYears);
        Scenario optimistic   = computeScenario("Optimiste",    OPTIMISTIC_RETURN,   monthlyInvestment, horizonYears);

        String explanation = callLLM(buildPrompt(monthlyInvestment, horizonYears, currency,
                conservative, moderate, optimistic, profile.getFinancialExperienceLevel().name(), goals.size()));

        return new FinancialProjectionResult(monthlyInvestment, horizonYears, currency,
                conservative, moderate, optimistic, explanation);
    }

    // ── Computation helpers ───────────────────────────────────────────────────

    private double computeMonthlyInvestment(BudgetService.BudgetSummary budget, List<Goal> goals) {
        double goalContributions = goals.stream()
                .filter(g -> g.getMonthlyContribution() != null)
                .mapToDouble(g -> g.getMonthlyContribution().doubleValue())
                .sum();
        if (goalContributions > 0) return goalContributions;
        double saving = budget.monthlyIncome().doubleValue() - budget.totalExpenses().doubleValue();
        return Math.max(saving, 0);
    }

    private int computeHorizon(List<Goal> goals) {
        // Use the longest goal target date as horizon, or default
        return goals.stream()
                .filter(g -> g.getTargetDate() != null)
                .mapToInt(g -> {
                    long months = java.time.temporal.ChronoUnit.MONTHS.between(
                            java.time.LocalDate.now(), g.getTargetDate());
                    return (int) Math.max(1, months / 12);
                })
                .max()
                .orElse(DEFAULT_HORIZON_YEARS);
    }

    private Scenario computeScenario(String label, double returnPct, double monthly, int years) {
        int n = years * 12;
        double invested = monthly * n;
        double finalCapital;
        if (returnPct <= 0 || monthly <= 0) {
            finalCapital = invested;
        } else {
            double r = returnPct / 100.0 / 12.0;
            finalCapital = monthly * (Math.pow(1 + r, n) - 1) / r;
        }
        double gain = finalCapital - invested;
        return new Scenario(label, returnPct,
                Math.round(invested * 100.0) / 100.0,
                Math.round(finalCapital * 100.0) / 100.0,
                Math.round(gain * 100.0) / 100.0);
    }

    private Scenario emptyScenario(String label) {
        return new Scenario(label, 0, 0, 0, 0);
    }

    // ── Prompt builder ────────────────────────────────────────────────────────

    private String buildPrompt(double monthly, int years, String currency,
                               Scenario cons, Scenario mod, Scenario opt,
                               String experienceLevel, int goalCount) {
        return String.format(
                "Voici mes projections financières personnalisées :%n%n" +
                "Investissement mensuel (basé sur mes contributions) : %.0f %s%n" +
                "Horizon de projection : %d ans%n" +
                "Niveau d'expérience : %s%n" +
                "Objectifs actifs : %d%n%n" +
                "Scénario conservateur (%.0f%% rendement annuel) :%n" +
                "  Capital investi : %.0f %s | Capital final : %.0f %s | Gains : %.0f %s%n%n" +
                "Scénario modéré (%.0f%% rendement annuel) :%n" +
                "  Capital investi : %.0f %s | Capital final : %.0f %s | Gains : %.0f %s%n%n" +
                "Scénario optimiste (%.0f%% rendement annuel) :%n" +
                "  Capital investi : %.0f %s | Capital final : %.0f %s | Gains : %.0f %s%n%n" +
                "Explique mes projections de façon personnalisée et motivante.",
                monthly, currency, years, experienceLevel.toLowerCase(), goalCount,
                cons.returnPct(), cons.capitalInvested(), currency, cons.capitalFinal(), currency, cons.interestGain(), currency,
                mod.returnPct(), mod.capitalInvested(), currency, mod.capitalFinal(), currency, mod.interestGain(), currency,
                opt.returnPct(), opt.capitalInvested(), currency, opt.capitalFinal(), currency, opt.interestGain(), currency);
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
            LOG.warning("HuggingFace financial-projection status=" + resp.statusCode());
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
