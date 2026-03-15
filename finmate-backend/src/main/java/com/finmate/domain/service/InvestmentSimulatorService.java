package com.finmate.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

@ApplicationScoped
public class InvestmentSimulatorService {

    private static final Logger LOG = Logger.getLogger(InvestmentSimulatorService.class.getName());

    private static final String CLAUDE_API_URL      = "https://api.anthropic.com/v1/messages";
    private static final String OPENAI_API_URL      = "https://api.openai.com/v1/chat/completions";
    private static final String HUGGINGFACE_API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String CLAUDE_MODEL        = "claude-opus-4-6";
    private static final String OPENAI_MODEL        = "gpt-4o";
    private static final int    MAX_TOKENS          = 800;

    private static final String SYSTEM_PROMPT =
            "Tu es FinMate, un assistant financier éducatif. " +
            "L'utilisateur vient de réaliser une simulation d'investissement. " +
            "Explique les résultats de façon claire, pédagogique et motivante. " +
            "Mets en valeur la puissance des intérêts composés et du temps. " +
            "Rappelle l'importance de la régularité (DCA). " +
            "Ne fais jamais de promesses de rendement ou de recommandations sur des titres spécifiques. " +
            "Réponds en français, en 3-4 paragraphes concis.";

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

    public InvestmentSimulatorService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public record SimulationResult(
            double monthlyInvestment,
            double expectedReturn,
            int horizonYears,
            double capitalInvested,
            double capitalFinal,
            double interestGain,
            String explanation
    ) {}

    public SimulationResult simulate(double monthlyInvestment, double expectedReturnPct, int horizonYears) {
        if (monthlyInvestment <= 0 || horizonYears <= 0) {
            return new SimulationResult(monthlyInvestment, expectedReturnPct, horizonYears, 0, 0, 0,
                    "Les paramètres de simulation doivent être supérieurs à zéro.");
        }

        int n = horizonYears * 12;
        double capitalInvested = monthlyInvestment * n;
        double capitalFinal;

        if (expectedReturnPct <= 0) {
            capitalFinal = capitalInvested;
        } else {
            double r = expectedReturnPct / 100.0 / 12.0;
            capitalFinal = monthlyInvestment * (Math.pow(1 + r, n) - 1) / r;
        }

        double interestGain = capitalFinal - capitalInvested;
        String explanation = callLLM(buildPrompt(monthlyInvestment, expectedReturnPct, horizonYears,
                capitalInvested, capitalFinal, interestGain));

        return new SimulationResult(monthlyInvestment, expectedReturnPct, horizonYears,
                Math.round(capitalInvested * 100.0) / 100.0,
                Math.round(capitalFinal * 100.0) / 100.0,
                Math.round(interestGain * 100.0) / 100.0,
                explanation);
    }

    // ── Prompt builder ────────────────────────────────────────────────────────

    private String buildPrompt(double monthly, double returnPct, int years,
                               double invested, double finalCapital, double gain) {
        return String.format(
                "Voici les résultats de ma simulation d'investissement :%n%n" +
                "- Investissement mensuel : %.0f €%n" +
                "- Rendement annuel estimé : %.1f%%%n" +
                "- Durée : %d ans (%d mois)%n%n" +
                "Résultats calculés :%n" +
                "- Capital investi (versements) : %.0f €%n" +
                "- Capital final (avec intérêts composés) : %.0f €%n" +
                "- Intérêts générés : %.0f € (+%.0f%%)%n%n" +
                "Explique ces résultats de façon motivante et pédagogique.",
                monthly, returnPct, years, years * 12,
                invested, finalCapital, gain,
                invested > 0 ? gain / invested * 100 : 0);
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
            LOG.warning("HuggingFace investment-simulation status=" + resp.statusCode());
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
