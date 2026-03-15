package com.finmate.domain.service;

import com.finmate.domain.exception.ValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.logging.Logger;

@ApplicationScoped
public class InvestmentEducationService {

    private static final Logger LOG = Logger.getLogger(InvestmentEducationService.class.getName());

    private static final String CLAUDE_API_URL      = "https://api.anthropic.com/v1/messages";
    private static final String OPENAI_API_URL      = "https://api.openai.com/v1/chat/completions";
    private static final String HUGGINGFACE_API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String CLAUDE_MODEL        = "claude-opus-4-6";
    private static final String OPENAI_MODEL        = "gpt-4o";
    private static final int    MAX_TOKENS          = 1500;

    private static final Set<String> ALLOWED_TOPICS = Set.of(
            "ETF", "DIVERSIFICATION", "COMPOUND_INTEREST", "LONG_TERM_INVESTING"
    );

    private static final String SYSTEM_PROMPT =
            "Tu es FinMate, un assistant financier éducatif pour jeunes investisseurs (18-25 ans). " +
            "Tu expliques des concepts financiers de manière simple, claire et pédagogique. " +
            "Ta réponse DOIT suivre exactement ce format JSON (sans markdown autour) :\n" +
            "{\n" +
            "  \"definition\": \"...\",\n" +
            "  \"example\": \"...\",\n" +
            "  \"risk\": \"...\",\n" +
            "  \"simpleSummary\": \"...\",\n" +
            "  \"keyPoints\": [\"...\", \"...\", \"...\"]\n" +
            "}\n" +
            "- definition : 2-3 phrases claires pour un débutant\n" +
            "- example : un exemple concret avec des chiffres réels\n" +
            "- risk : les risques principaux à connaître\n" +
            "- simpleSummary : 1 phrase résumant l'essentiel\n" +
            "- keyPoints : 3 points clés à retenir (tableau de strings)\n" +
            "Réponds uniquement en français. Ne fais aucune recommandation sur des titres spécifiques.";

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

    public InvestmentEducationService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public record InvestmentExplanation(
            String topic,
            String topicLabel,
            String definition,
            String example,
            String risk,
            String simpleSummary,
            java.util.List<String> keyPoints
    ) {}

    public InvestmentExplanation explain(String topic) {
        String normalized = topic.toUpperCase().trim().replace(" ", "_");
        if (!ALLOWED_TOPICS.contains(normalized)) {
            throw new ValidationException("Sujet non reconnu : " + topic);
        }

        String topicLabel = topicLabel(normalized);
        String userMessage = "Explique-moi le concept financier suivant : " + topicLabel;
        String rawJson = callLLM(userMessage);
        return parseExplanation(normalized, topicLabel, rawJson);
    }

    // ── Parser ────────────────────────────────────────────────────────────────

    private InvestmentExplanation parseExplanation(String topic, String topicLabel, String json) {
        String definition   = extractField(json, "definition");
        String example      = extractField(json, "example");
        String risk         = extractField(json, "risk");
        String simpleSummary = extractField(json, "simpleSummary");
        java.util.List<String> keyPoints = extractArray(json, "keyPoints");
        return new InvestmentExplanation(topic, topicLabel, definition, example, risk, simpleSummary, keyPoints);
    }

    private String extractField(String json, String field) {
        String marker = "\"" + field + "\"";
        int idx = json.indexOf(marker);
        if (idx == -1) return "";
        int colon = json.indexOf(":", idx + marker.length());
        if (colon == -1) return "";
        int valueStart = json.indexOf("\"", colon + 1);
        if (valueStart == -1) return "";
        int valueEnd = findEndOfString(json, valueStart + 1);
        return json.substring(valueStart + 1, valueEnd)
                .replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private java.util.List<String> extractArray(String json, String field) {
        String marker = "\"" + field + "\"";
        int idx = json.indexOf(marker);
        if (idx == -1) return java.util.List.of();
        int arrStart = json.indexOf("[", idx + marker.length());
        int arrEnd   = json.indexOf("]", arrStart);
        if (arrStart == -1 || arrEnd == -1) return java.util.List.of();
        String inner = json.substring(arrStart + 1, arrEnd);
        java.util.List<String> result = new java.util.ArrayList<>();
        int i = 0;
        while (i < inner.length()) {
            int start = inner.indexOf("\"", i);
            if (start == -1) break;
            int end = findEndOfString(inner, start + 1);
            result.add(inner.substring(start + 1, end).replace("\\n", "\n").replace("\\\"", "\""));
            i = end + 1;
        }
        return result;
    }

    // ── Topic labels ──────────────────────────────────────────────────────────

    private String topicLabel(String topic) {
        return switch (topic) {
            case "ETF"                -> "ETF (Exchange-Traded Fund)";
            case "DIVERSIFICATION"    -> "Diversification";
            case "COMPOUND_INTEREST"  -> "Intérêts composés";
            case "LONG_TERM_INVESTING" -> "Investissement long terme";
            default                   -> topic;
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
            LOG.warning("HuggingFace investment-education status=" + resp.statusCode());
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
