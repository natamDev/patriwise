package com.finmate.domain.port;

import com.finmate.domain.model.GoalContribution;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface GoalContributionRepository {
    GoalContribution save(GoalContribution contribution);
    List<GoalContribution> findByGoalId(UUID goalId);
    BigDecimal sumByGoalId(UUID goalId);
}
