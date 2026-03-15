package com.finmate.domain.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FomoDetectionService {

    public record FomoAlert(
            String biasType,
            String triggerCategory,
            String explanation,
            String alternative
    ) {}

    // Catégories de signaux FOMO
    private static final List<String> RAPID_PRICE_SIGNALS = List.of(
            "explose", "x10", "x5", "x2", "100%", "200%", "multiplié", "flambe", "flambée",
            "pump", "moon", "moone", "record", "plus haut", "tout temps haut",
            "monte en flèche", "hausse rapide", "vite monté", "a décollé", "décollé"
    );

    private static final List<String> SOCIAL_HYPE_SIGNALS = List.of(
            "tout le monde", "tous le monde", "twitter", "reddit", "tiktok", "instagram",
            "influenceur", "viral", "buzz", "tendance", "trend", "on parle",
            "tout le monde en parle", "mes amis", "mon collègue", "j'ai vu que"
    );

    private static final List<String> SHORT_TERM_SIGNALS = List.of(
            "maintenant", "tout de suite", "urgent", "vite", "rapidement",
            "aujourd'hui", "ce soir", "demain", "cette semaine",
            "avant que", "avant qu'il", "avant la fin", "ne pas rater",
            "dernière chance", "opportunité unique", "maintenant ou jamais"
    );

    public Optional<FomoAlert> detect(String message) {
        String lower = message.toLowerCase();

        int score = 0;
        String dominantCategory = null;

        if (containsAny(lower, RAPID_PRICE_SIGNALS)) {
            score++;
            dominantCategory = "rapid_price_increase";
        }
        if (containsAny(lower, SOCIAL_HYPE_SIGNALS)) {
            score++;
            if (dominantCategory == null) dominantCategory = "social_media_hype";
        }
        if (containsAny(lower, SHORT_TERM_SIGNALS)) {
            score++;
            if (dominantCategory == null) dominantCategory = "short_term_intent";
        }

        if (score == 0) return Optional.empty();

        String explanation = buildExplanation(dominantCategory, score);
        String alternative = buildAlternative(dominantCategory);

        return Optional.of(new FomoAlert("FOMO", dominantCategory, explanation, alternative));
    }

    private boolean containsAny(String text, List<String> keywords) {
        return keywords.stream().anyMatch(text::contains);
    }

    private String buildExplanation(String category, int intensity) {
        return switch (category) {
            case "rapid_price_increase" ->
                    "⚠️ Attention au FOMO ! Tu mentionnes une forte hausse récente. Les actifs qui ont beaucoup monté sont souvent achetés au mauvais moment — les hausses rapides sont souvent suivies de corrections. Attends 48h avant d'agir.";
            case "social_media_hype" ->
                    "⚠️ Attention au FOMO ! Les réseaux sociaux amplifient l'enthousiasme autour d'actifs en vogue. Quand « tout le monde » en parle, c'est souvent que l'opportunité est déjà passée.";
            case "short_term_intent" ->
                    "⚠️ Attention au FOMO ! Tu sembles vouloir agir rapidement. L'urgence est un signal d'alarme en investissement. Les bonnes décisions financières se prennent rarement dans l'urgence.";
            default ->
                    "⚠️ Attention au FOMO ! Plusieurs signaux dans ton message suggèrent une décision émotionnelle plutôt que rationnelle. Prends le temps de réfléchir.";
        };
    }

    private String buildAlternative(String category) {
        return switch (category) {
            case "rapid_price_increase" ->
                    "Pose-toi cette question : aurais-tu voulu acheter cet actif il y a 6 mois ? Si non, c'est probablement du FOMO. Préfère un ETF diversifié en DCA régulier.";
            case "social_media_hype" ->
                    "Les conseils d'investissement sur les réseaux sociaux sont rarement fiables. Concentre-toi sur ta stratégie long terme plutôt que sur les tendances du moment.";
            case "short_term_intent" ->
                    "Mets en place une règle : attends toujours 48h avant tout achat impulsif. Si l'idée reste bonne après 2 jours, alors agis — mais avec un montant raisonnable.";
            default ->
                    "Reviens à tes fondamentaux : investis régulièrement un montant fixe dans des actifs diversifiés, sans te laisser influencer par les émotions.";
        };
    }
}
