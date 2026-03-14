package com.finmate.application.dto;

import com.finmate.domain.model.Goal.GoalType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UpdateGoalDto {
    private String goalName;
    private GoalType goalType;
    private BigDecimal targetAmount;
    private LocalDate targetDate;
    private BigDecimal monthlyContribution;
}
