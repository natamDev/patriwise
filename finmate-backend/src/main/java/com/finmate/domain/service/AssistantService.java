package com.finmate.domain.service;

import com.finmate.domain.model.AssistantConversation;
import com.finmate.domain.model.AssistantMessage;
import com.finmate.domain.model.ConceptCard;
import com.finmate.domain.port.AssistantConversationRepository;
import com.finmate.domain.port.AssistantMessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.logging.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AssistantService {

    private static final Logger LOG = Logger.getLogger(AssistantService.class.getName());

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String HUGGINGFACE_API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String CLAUDE_MODEL = "claude-opus-4-6";
    private static final String OPENAI_MODEL = "gpt-4o";
    private static final int MAX_TOKENS = 1024;
    private static final int MAX_HISTORY = 20;

    private static final String SYSTEM_PROMPT =
            "Tu es FinMate, un assistant financier éducatif pour jeunes investisseurs (18-25 ans). " +
            "Ton rôle est d'expliquer des concepts financiers de manière simple, claire et pédagogique. " +
            "Tu aides l'utilisateur à comprendre l'investissement passif, la gestion du risque, les ETF, le DCA et la psychologie financière. " +
            "Tu ne fais jamais de recommandations d'investissement spécifiques sur des titres individuels. " +
            "Tu ne prédi jamais les cours des marchés. " +
            "Tu rappelles toujours que tes réponses sont éducatives et ne constituent pas du conseil financier professionnel. " +
            "Tu réponds toujours en français, avec un ton bienveillant et encourageant. " +
            "Tes réponses sont concises (3-5 paragraphes maximum) et adaptées à un public débutant.";

    private final AssistantConversationRepository conversationRepository;
    private final AssistantMessageRepository messageRepository;
    private final ConceptService conceptService;
    private final CoachingService coachingService;
    private final HttpClient httpClient;

    @ConfigProperty(name = "finmate.ai.provider", defaultValue = "claude")
    String aiProvider;

    @ConfigProperty(name = "finmate.claude.api-key", defaultValue = "changeme")
    String claudeApiKey;

    @ConfigProperty(name = "finmate.openai.api-key", defaultValue = "changeme")
    String openaiApiKey;

    @ConfigProperty(name = "finmate.huggingface.api-key", defaultValue = "changeme")
    String huggingfaceApiKey;

    @ConfigProperty(name = "finmate.huggingface.model", defaultValue = "mistralai/Mistral-7B-Instruct-v0.3")
    String huggingfaceModel;

    public AssistantService(
            AssistantConversationRepository conversationRepository,
            AssistantMessageRepository messageRepository,
            ConceptService conceptService,
            CoachingService coachingService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.conceptService = conceptService;
        this.coachingService = coachingService;
        this.httpClient = HttpClient.newHttpClient();
    }

    public record ChatResult(UUID conversationId, String reply, ConceptCard conceptCard) {}

    public ChatResult chat(UUID userId, UUID conversationId, String userMessage) {
        AssistantConversation conversation;
        if (conversationId != null) {
            conversation = conversationRepository.findById(conversationId)
                    .filter(c -> c.getUserId().equals(userId))
                    .orElseGet(() -> createConversation(userId));
        } else {
            conversation = createConversation(userId);
        }

        AssistantMessage userMsg = new AssistantMessage();
        userMsg.setConversationId(conversation.getId());
        userMsg.setRole("user");
        userMsg.setContent(userMessage);
        userMsg.setCreatedAt(LocalDateTime.now());
        messageRepository.save(userMsg);

        List<AssistantMessage> history = messageRepository.findByConversationId(conversation.getId());
        int start = Math.max(0, history.size() - MAX_HISTORY);
        List<AssistantMessage> messages = history.subList(start, history.size());

        String userContext = coachingService.buildUserContext(userId);
        String reply = switch (aiProvider.toLowerCase()) {
            case "openai" -> callOpenAI(messages, userContext);
            case "huggingface" -> callHuggingFace(messages, userContext);
            default -> callClaude(messages, userContext);
        };

        AssistantMessage assistantMsg = new AssistantMessage();
        assistantMsg.setConversationId(conversation.getId());
        assistantMsg.setRole("assistant");
        assistantMsg.setContent(reply);
        assistantMsg.setCreatedAt(LocalDateTime.now());
        messageRepository.save(assistantMsg);

        Optional<ConceptCard> conceptCard = conceptService.detect(userMessage);
        return new ChatResult(conversation.getId(), reply, conceptCard.orElse(null));
    }

    private AssistantConversation createConversation(UUID userId) {
        AssistantConversation conv = new AssistantConversation();
        conv.setUserId(userId);
        conv.setCreatedAt(LocalDateTime.now());
        return conversationRepository.save(conv);
    }

    // ── Claude ──────────────────────────────────────────────────────────────

    private String callClaude(List<AssistantMessage> messages, String userContext) {
        String messagesJson = buildMessagesJson(messages);
        String fullSystem = userContext.isEmpty() ? SYSTEM_PROMPT : SYSTEM_PROMPT + "\n\n" + userContext;
        String systemEscaped = escapeJson(fullSystem);
        String body = "{\"model\":\"" + CLAUDE_MODEL + "\",\"max_tokens\":" + MAX_TOKENS
                + ",\"system\":\"" + systemEscaped + "\",\"messages\":" + messagesJson + "}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CLAUDE_API_URL))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", claudeApiKey)
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur Claude API : " + response.statusCode());
            }
            // {"content":[{"type":"text","text":"..."}],...}
            return extractJsonString(response.body(), "\"text\":");
        } catch (Exception e) {
            throw new RuntimeException("Impossible de contacter l'assistant. Veuillez réessayer.", e);
        }
    }

    // ── OpenAI ──────────────────────────────────────────────────────────────

    private String callOpenAI(List<AssistantMessage> messages, String userContext) {
        String fullSystem = userContext.isEmpty() ? SYSTEM_PROMPT : SYSTEM_PROMPT + "\n\n" + userContext;
        StringBuilder msgsBuilder = new StringBuilder("[");
        msgsBuilder.append("{\"role\":\"system\",\"content\":\"").append(escapeJson(fullSystem)).append("\"}");
        for (AssistantMessage m : messages) {
            msgsBuilder.append(",{\"role\":\"").append(m.getRole())
                    .append("\",\"content\":\"").append(escapeJson(m.getContent())).append("\"}");
        }
        msgsBuilder.append("]");

        String body = "{\"model\":\"" + OPENAI_MODEL + "\",\"max_tokens\":" + MAX_TOKENS
                + ",\"messages\":" + msgsBuilder + "}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openaiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur OpenAI API : " + response.statusCode());
            }
            // {"choices":[{"message":{"role":"assistant","content":"..."}}],...}
            return extractJsonString(response.body(), "\"content\":");
        } catch (Exception e) {
            throw new RuntimeException("Impossible de contacter l'assistant. Veuillez réessayer.", e);
        }
    }

    // ── HuggingFace ─────────────────────────────────────────────────────────

    private String callHuggingFace(List<AssistantMessage> messages, String userContext) {
        String fullSystem = userContext.isEmpty() ? SYSTEM_PROMPT : SYSTEM_PROMPT + "\n\n" + userContext;
        StringBuilder msgsBuilder = new StringBuilder("[");
        msgsBuilder.append("{\"role\":\"system\",\"content\":\"").append(escapeJson(fullSystem)).append("\"}");
        for (AssistantMessage m : messages) {
            msgsBuilder.append(",{\"role\":\"").append(m.getRole())
                    .append("\",\"content\":\"").append(escapeJson(m.getContent())).append("\"}");
        }
        msgsBuilder.append("]");

        String body = "{\"model\":\"" + huggingfaceModel + "\",\"max_tokens\":" + MAX_TOKENS
                + ",\"messages\":" + msgsBuilder + "}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HUGGINGFACE_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + huggingfaceApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            LOG.warning("HuggingFace status=" + response.statusCode() + " body=" + response.body());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur HuggingFace API " + response.statusCode() + " : " + response.body());
            }
            // Même format de réponse qu'OpenAI
            return extractJsonString(response.body(), "\"content\":");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Impossible de contacter l'assistant : " + e.getMessage(), e);
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private String buildMessagesJson(List<AssistantMessage> messages) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < messages.size(); i++) {
            AssistantMessage m = messages.get(i);
            sb.append("{\"role\":\"").append(m.getRole())
              .append("\",\"content\":\"").append(escapeJson(m.getContent())).append("\"}");
            if (i < messages.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String extractJsonString(String json, String marker) {
        int markerIdx = json.indexOf(marker);
        if (markerIdx == -1) throw new RuntimeException("Réponse inattendue de l'assistant.");
        int start = json.indexOf("\"", markerIdx + marker.length()) + 1;
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
