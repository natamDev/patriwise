package com.finmate.domain.service;

import com.finmate.domain.model.Goal;
import com.finmate.domain.model.GoalContribution;
import com.finmate.domain.model.SavingStreak;
import com.finmate.domain.port.GoalContributionRepository;
import com.finmate.domain.port.GoalRepository;
import com.finmate.domain.port.SavingStreakRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

@ApplicationScoped
public class SavingStreakService {

    private final GoalRepository goalRepository;
    private final GoalContributionRepository contributionRepository;
    private final SavingStreakRepository streakRepository;

    public SavingStreakService(GoalRepository goalRepository,
                               GoalContributionRepository contributionRepository,
                               SavingStreakRepository streakRepository) {
        this.goalRepository = goalRepository;
        this.contributionRepository = contributionRepository;
        this.streakRepository = streakRepository;
    }

    @Transactional
    public SavingStreak computeAndSave(UUID userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);

        TreeSet<YearMonth> activeMonths = new TreeSet<>();
        for (Goal goal : goals) {
            List<GoalContribution> contributions = contributionRepository.findByGoalId(goal.getId());
            for (GoalContribution c : contributions) {
                activeMonths.add(YearMonth.from(c.getContributionDate()));
            }
        }

        int currentStreak = 0;
        int longestStreak = 0;
        int streak = 0;
        YearMonth previous = null;

        for (YearMonth month : activeMonths) {
            if (previous != null && month.equals(previous.plusMonths(1))) {
                streak++;
            } else {
                streak = 1;
            }
            if (streak > longestStreak) longestStreak = streak;
            previous = month;
        }

        // Current streak: only counts if last active month is current or previous month
        YearMonth now = YearMonth.now();
        if (previous != null && (previous.equals(now) || previous.equals(now.minusMonths(1)))) {
            currentStreak = streak;
        } else {
            currentStreak = 0;
        }

        SavingStreak result = new SavingStreak(userId, currentStreak, longestStreak);
        return streakRepository.save(result);
    }
}
