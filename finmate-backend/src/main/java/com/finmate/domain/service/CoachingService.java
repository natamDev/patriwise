package com.finmate.domain.service;

import com.finmate.domain.model.AssistantRecommendation;
import com.finmate.domain.model.AssistantRecommendation.RecommendationType;
import com.finmate.domain.model.Goal;
import com.finmate.domain.port.AssistantRecommendationRepository;
import com.finmate.domain.port.GoalRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CoachingService {

    private final FinancialProfileService profileService;
    private final BudgetService budgetService;
    private final GoalRepository goalRepository;
    private final AssistantRecommendationRepository recommendationRepository;

    public CoachingService(
            FinancialProfileService profileService,
            BudgetService budgetService,
            GoalRepository goalRepository,
            AssistantRecommendationRepository recommendationRepository) {
        this.profileService = profileService;
        this.budgetService = budgetService;
        this.goalRepository = goalRepository;
        this.recommendationRepository = recommendationRepository;
    }

    public AssistantRecommendation generateCoaching(UUID userId) {
        // 1 — Profil incomplet
        var profileOpt = profileService.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            return save(userId, RecommendationType.COMPLETE_PROFILE,
                    "Ton profil financier n'est pas encore renseigné. Sans ces informations, il m'est impossible de personnaliser mes conseils à ta situation.",
                    "Complète ton profil financier dans l'onglet Profil (revenu, situation, expérience).");
        }

        var profile = profileOpt.get();
        var budget = budgetService.computeSummary(userId, YearMonth.now());
        List<Goal> goals = goalRepository.findByUserId(userId);

        double savingsRate = budget.savingsRate();
        double income = budget.monthlyIncome().doubleValue();
        double expenses = budget.totalExpenses().doubleValue();

        // 2 — Dépenses excessives (> 90 % du revenu)
        if (income > 0 && expenses / income > 0.90) {
            int expensePct = (int) Math.round(expenses / income * 100);
            return save(userId, RecommendationType.REDUCE_EXPENSES,
                    String.format(
                            "Ce mois-ci, tes dépenses représentent %d %% de ton revenu (%.0f € dépensés pour %.0f € de revenu). " +
                            "Ce niveau te laisse très peu de marge pour épargner ou investir. " +
                            "Identifier les dépenses non essentielles est la première étape pour reprendre le contrôle.",
                            expensePct, expenses, income),
                    "Passe en revue tes dépenses ce mois-ci et identifie 1 ou 2 postes à réduire (abonnements, sorties, alimentation).");
        }

        // 3 — Taux d'épargne trop faible (< 10 %)
        if (income > 0 && savingsRate < 0.10) {
            int savingsPct = (int) Math.round(savingsRate * 100);
            return save(userId, RecommendationType.INCREASE_SAVINGS,
                    String.format(
                            "Ton taux d'épargne actuel est de %d %%. L'objectif recommandé est d'épargner au minimum 10 %% de ses revenus. " +
                            "Même une petite augmentation régulière fait une grande différence sur le long terme grâce aux intérêts composés.",
                            savingsPct),
                    "Mets en place un virement automatique de " + (int)(income * 0.10) + " € le jour de ta paie vers un compte épargne dédié.");
        }

        // 4 — Aucun objectif d'investissement
        boolean hasInvestmentGoal = goals.stream()
                .anyMatch(g -> g.getGoalType() == Goal.GoalType.INVESTMENT);
        if (goals.isEmpty() || !hasInvestmentGoal) {
            return save(userId, RecommendationType.START_INVESTING,
                    "Tu n'as pas encore d'objectif d'investissement. " +
                    "Commencer à investir tôt, même de petites sommes, est l'un des meilleurs leviers pour construire un patrimoine sur le long terme. " +
                    "Un ETF Monde en DCA est le point de départ idéal pour un débutant.",
                    "Crée un objectif d'investissement dans l'onglet Objectifs et définis une contribution mensuelle, même modeste (10–50 €).");
        }

        // 5 — Objectifs sans contribution ce mois-ci
        if (goals.stream().anyMatch(g -> g.getMonthlyContribution() == null
                || g.getMonthlyContribution().doubleValue() == 0)) {
            return save(userId, RecommendationType.CONTRIBUTE_TO_GOALS,
                    "Tu as des objectifs d'épargne mais certains n'ont pas de contribution mensuelle définie. " +
                    "La régularité est la clé : investir un montant fixe chaque mois (DCA) réduit le risque et lisse ton prix moyen d'achat.",
                    "Définis une contribution mensuelle pour chacun de tes objectifs, même symbolique, pour ancrer la discipline d'épargne.");
        }

        // 6 — Tout va bien
        return save(userId, RecommendationType.WELL_DONE,
                String.format(
                        "Bravo ! Ton taux d'épargne est de %d %% ce mois-ci et tu as %d objectif(s) actif(s). " +
                        "Tu es sur la bonne voie. Continue à investir régulièrement et évite de regarder ton portefeuille trop souvent.",
                        (int) Math.round(savingsRate * 100), goals.size()),
                "Continue ton DCA mensuel et reviens consulter ton coaching dans 30 jours pour suivre ta progression.");
    }

    /** Construit un résumé du contexte utilisateur pour enrichir le prompt du chat. */
    public String buildUserContext(UUID userId) {
        var profileOpt = profileService.findByUserId(userId);
        if (profileOpt.isEmpty()) return "";

        var profile = profileOpt.get();
        var budget = budgetService.computeSummary(userId, YearMonth.now());
        List<Goal> goals = goalRepository.findByUserId(userId);

        int savingsPct = (int) Math.round(budget.savingsRate() * 100);

        return String.format(
                "[Contexte utilisateur]\n" +
                "Revenu mensuel : %.0f %s\n" +
                "Dépenses ce mois : %.0f %s\n" +
                "Taux d'épargne : %d %%\n" +
                "Objectifs actifs : %d\n" +
                "Expérience financière : %s\n",
                profile.getMonthlyIncome().doubleValue(), profile.getCurrency(),
                budget.totalExpenses().doubleValue(), profile.getCurrency(),
                savingsPct,
                goals.size(),
                profile.getFinancialExperienceLevel().toString().toLowerCase());
    }

    private AssistantRecommendation save(UUID userId, RecommendationType type, String message, String suggestedAction) {
        AssistantRecommendation r = new AssistantRecommendation();
        r.setUserId(userId);
        r.setRecommendationType(type);
        r.setMessage(message);
        r.setSuggestedAction(suggestedAction);
        r.setCreatedAt(LocalDateTime.now());
        return recommendationRepository.save(r);
    }
}
