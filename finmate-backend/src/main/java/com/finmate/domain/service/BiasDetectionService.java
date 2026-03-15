package com.finmate.domain.service;

import com.finmate.domain.model.BehavioralEvent;
import com.finmate.domain.port.BehavioralEventRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Detects behavioral finance biases in user messages.
 * Covers: FOMO (delegated to FomoDetectionService), OVERCONFIDENCE,
 * CONFIRMATION_BIAS, LOSS_AVERSION.
 * Persists detected events to behavioral_events table.
 */
@ApplicationScoped
public class BiasDetectionService {

    private final BehavioralEventRepository eventRepository;

    public BiasDetectionService(BehavioralEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public record BiasAlert(
            String biasType,
            String biasLabel,
            String explanation,
            String alternative
    ) {}

    public Optional<BiasAlert> detect(String message, UUID userId) {
        String lower = message.toLowerCase();

        Optional<BiasAlert> alert = detectOverconfidence(lower)
                .or(() -> detectConfirmationBias(lower))
                .or(() -> detectLossAversion(lower));

        alert.ifPresent(a -> persist(userId, a, message));
        return alert;
    }

    // ── Overconfidence ────────────────────────────────────────────────────────

    private Optional<BiasAlert> detectOverconfidence(String lower) {
        List<String> signals = List.of(
                "je suis sûr", "c'est certain", "impossible de perdre", "j'en suis certain",
                "je ne peux pas me tromper", "risque zéro", "valeur sûre", "aucun risque",
                "je vais forcément", "c'est garanti", "sans risque", "je suis convaincu que ça va monter",
                "ça ne peut qu'augmenter", "ça va exploser"
        );
        if (signals.stream().anyMatch(lower::contains)) {
            return Optional.of(new BiasAlert(
                    "OVERCONFIDENCE",
                    "Excès de confiance",
                    "Tu sembles très confiant dans cette décision. L'excès de confiance (overconfidence bias) est l'un des biais les plus courants chez les investisseurs : nous surestimoons nos connaissances et sous-estimons l'incertitude des marchés. Même les experts se trompent régulièrement.",
                    "Avant d'investir, pose-toi ces questions : Quelles informations me manquent ? Quel est le scénario adverse ? Quelle portion de mon épargne suis-je prêt à risquer ? La diversification reste la meilleure protection contre la certitude excessive."
            ));
        }
        return Optional.empty();
    }

    // ── Confirmation bias ─────────────────────────────────────────────────────

    private Optional<BiasAlert> detectConfirmationBias(String lower) {
        List<String> signals = List.of(
                "tout le monde dit", "c'est évident", "j'ai lu partout", "tout prouve que",
                "c'est logique que", "tout le monde sait", "les experts disent tous",
                "c'est unanime", "personne ne doute", "il suffit de voir que",
                "ça confirme ce que je pensais", "j'avais raison depuis le début"
        );
        if (signals.stream().anyMatch(lower::contains)) {
            return Optional.of(new BiasAlert(
                    "CONFIRMATION_BIAS",
                    "Biais de confirmation",
                    "Tu cherches peut-être des informations qui confirment une conviction déjà établie. Le biais de confirmation nous pousse à ignorer les données qui contredisent nos croyances et à surpondérer celles qui les renforcent — c'est une illusion de certitude très dangereuse en investissement.",
                    "Cherche activement des arguments contraires à ta thèse. Lis des analyses pessimistes sur l'actif qui t'intéresse. Si tu ne trouves aucun contre-argument valable, reconsidère ta position."
            ));
        }
        return Optional.empty();
    }

    // ── Loss aversion ─────────────────────────────────────────────────────────

    private Optional<BiasAlert> detectLossAversion(String lower) {
        List<String> signals = List.of(
                "j'ai peur de perdre", "si ça descend je vends", "je préfère ne pas risquer",
                "trop risqué pour moi", "mieux vaut ne pas", "j'attends que ça remonte",
                "je vends dès que", "je ne supporte pas de perdre", "j'ai perdu et je veux récupérer",
                "je dois me rattraper", "vendre à perte", "couper les pertes maintenant",
                "trop stressant", "j'ai peur du marché"
        );
        if (signals.stream().anyMatch(lower::contains)) {
            return Optional.of(new BiasAlert(
                    "LOSS_AVERSION",
                    "Aversion à la perte",
                    "Tu ressens peut-être l'aversion à la perte : la douleur de perdre 100 € est psychologiquement deux fois plus forte que le plaisir d'en gagner 100. Ce biais pousse à vendre trop tôt lors des baisses et à conserver des positions perdantes trop longtemps. C'est l'ennemi numéro 1 de l'investisseur long terme.",
                    "Rappelle-toi que les fluctuations à court terme sont normales. Un portefeuille diversifié en ETF a toujours récupéré sur des horizons de 10+ ans. Évite de regarder ton portefeuille quotidiennement et reste fidèle à ta stratégie de DCA."
            ));
        }
        return Optional.empty();
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    private void persist(UUID userId, BiasAlert alert, String triggerMessage) {
        try {
            BehavioralEvent event = new BehavioralEvent();
            event.setUserId(userId);
            event.setBiasType(alert.biasType());
            event.setTriggerMessage(triggerMessage.length() > 500
                    ? triggerMessage.substring(0, 500) : triggerMessage);
            event.setExplanation(alert.explanation());
            event.setDetectedAt(LocalDateTime.now());
            eventRepository.save(event);
        } catch (Exception e) {
            // Non-blocking: bias detection should never break the chat flow
        }
    }
}
