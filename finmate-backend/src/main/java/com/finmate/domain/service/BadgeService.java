package com.finmate.domain.service;

import com.finmate.domain.model.Badge;
import com.finmate.domain.model.Goal;
import com.finmate.domain.model.UserBadge;
import com.finmate.domain.port.GoalContributionRepository;
import com.finmate.domain.port.GoalRepository;
import com.finmate.domain.port.UserBadgeRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class BadgeService {

    // Badge definitions (in-code, no DB table needed for definitions)
    public static final List<Badge> ALL_BADGES = List.of(
            new Badge("first_goal",            "Premier objectif",        "Tu as créé ton premier objectif d'épargne.",                "🎯"),
            new Badge("first_100_saved",        "100 € épargnés",         "Tu as contribué 100 € ou plus à tes objectifs.",            "💰"),
            new Badge("first_investment_plan",  "Investisseur débutant",  "Tu as créé un objectif d'investissement.",                  "📈"),
            new Badge("budget_master",          "Maître du budget",       "Tes dépenses sont inférieures à 70 % de tes revenus.",      "🏦"),
            new Badge("saver_streak",           "Épargnant régulier",     "Tu as contribué à tes objectifs 3 mois de suite.",          "🔥")
    );

    private final GoalRepository goalRepository;
    private final GoalContributionRepository contributionRepository;
    private final UserBadgeRepository userBadgeRepository;

    public BadgeService(
            GoalRepository goalRepository,
            GoalContributionRepository contributionRepository,
            UserBadgeRepository userBadgeRepository) {
        this.goalRepository = goalRepository;
        this.contributionRepository = contributionRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    public record BadgeStatus(Badge badge, boolean unlocked, LocalDateTime unlockedAt) {}

    /** Vérifie et attribue les badges manquants, puis retourne tous les statuts. */
    public List<BadgeStatus> checkAndGetBadges(UUID userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);
        Set<String> alreadyUnlocked = userBadgeRepository.findByUserId(userId)
                .stream().map(UserBadge::getBadgeId).collect(Collectors.toSet());

        // Calcul des conditions
        boolean hasGoal = !goals.isEmpty();

        BigDecimal totalSaved = goals.stream()
                .map(g -> contributionRepository.sumByGoalId(g.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean hasInvestmentGoal = goals.stream()
                .anyMatch(g -> g.getGoalType() == Goal.GoalType.INVESTMENT);

        Map<String, Boolean> conditions = Map.of(
                "first_goal",           hasGoal,
                "first_100_saved",      totalSaved.compareTo(new BigDecimal("100")) >= 0,
                "first_investment_plan", hasInvestmentGoal,
                "budget_master",        false, // requires budget access, skipped here
                "saver_streak",         false  // requires streak data, skipped here
        );

        // Attribution des nouveaux badges
        for (Map.Entry<String, Boolean> entry : conditions.entrySet()) {
            String badgeId = entry.getKey();
            if (entry.getValue() && !alreadyUnlocked.contains(badgeId)) {
                award(userId, badgeId);
                alreadyUnlocked.add(badgeId);
            }
        }

        // Construction de la liste complète avec statut
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(userId);
        Map<String, LocalDateTime> unlockedMap = userBadges.stream()
                .collect(Collectors.toMap(UserBadge::getBadgeId, UserBadge::getUnlockedAt));

        return ALL_BADGES.stream()
                .map(b -> new BadgeStatus(b, unlockedMap.containsKey(b.getId()), unlockedMap.get(b.getId())))
                .toList();
    }

    public void award(UUID userId, String badgeId) {
        if (userBadgeRepository.existsByUserIdAndBadgeId(userId, badgeId)) return;
        UserBadge ub = new UserBadge();
        ub.setUserId(userId);
        ub.setBadgeId(badgeId);
        ub.setUnlockedAt(LocalDateTime.now());
        userBadgeRepository.save(ub);
    }
}
