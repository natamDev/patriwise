package com.finmate.domain.service;

import com.finmate.domain.exception.ResourceNotFoundException;
import com.finmate.domain.exception.ValidationException;
import com.finmate.domain.model.Goal;
import com.finmate.domain.model.GoalContribution;
import com.finmate.domain.port.GoalContributionRepository;
import com.finmate.domain.port.GoalRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class GoalProgressService {

    private final GoalRepository goalRepository;
    private final GoalContributionRepository contributionRepository;

    public GoalProgressService(GoalRepository goalRepository, GoalContributionRepository contributionRepository) {
        this.goalRepository = goalRepository;
        this.contributionRepository = contributionRepository;
    }

    public GoalProgress getProgress(UUID goalId, UUID userId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Objectif introuvable."));
        if (!goal.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Objectif introuvable.");
        }
        BigDecimal saved = contributionRepository.sumByGoalId(goalId);
        return computeProgress(goal, saved);
    }

    public GoalContribution addContribution(UUID goalId, UUID userId, BigDecimal amount) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Objectif introuvable."));
        if (!goal.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Objectif introuvable.");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new ValidationException("Le montant de la contribution doit être supérieur à zéro.");
        }
        GoalContribution contribution = new GoalContribution();
        contribution.setGoalId(goalId);
        contribution.setAmount(amount);
        contribution.setContributionDate(LocalDate.now());
        return contributionRepository.save(contribution);
    }

    private GoalProgress computeProgress(Goal goal, BigDecimal saved) {
        BigDecimal target = goal.getTargetAmount();
        BigDecimal remaining = target.subtract(saved).max(BigDecimal.ZERO);
        int percent = target.compareTo(BigDecimal.ZERO) > 0
                ? saved.divide(target, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                        .min(BigDecimal.valueOf(100)).intValue()
                : 0;

        BigDecimal contribution = goal.getMonthlyContribution();
        Integer monthsNeeded = null;
        LocalDate estimatedCompletionDate = null;
        if (remaining.compareTo(BigDecimal.ZERO) > 0
                && contribution != null && contribution.compareTo(BigDecimal.ZERO) > 0) {
            monthsNeeded = remaining.divide(contribution, 0, RoundingMode.CEILING).intValue();
            estimatedCompletionDate = LocalDate.now().plusMonths(monthsNeeded);
        }

        return new GoalProgress(goal.getId(), saved, target, remaining, percent, monthsNeeded, estimatedCompletionDate);
    }

    public record GoalProgress(
            UUID goalId,
            BigDecimal savedAmount,
            BigDecimal targetAmount,
            BigDecimal remainingAmount,
            int percent,
            Integer monthsNeeded,
            LocalDate estimatedCompletionDate
    ) {}
}
