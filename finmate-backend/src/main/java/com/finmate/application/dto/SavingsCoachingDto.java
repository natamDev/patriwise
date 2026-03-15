package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SavingsCoachingDto {

    private String coaching;
    private List<GoalSummary> goals;

    @Data
    @NoArgsConstructor
    public static class GoalSummary {
        private UUID goalId;
        private String goalName;
        private String goalType;
        private double targetAmount;
        private double savedAmount;
        private double remainingAmount;
        private int percent;
        private Integer monthsNeeded;
        private LocalDate estimatedCompletionDate;
        private double monthlyContribution;
        private Double optimizedContribution;
    }
}
