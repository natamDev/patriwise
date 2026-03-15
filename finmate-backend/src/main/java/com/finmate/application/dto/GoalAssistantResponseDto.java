package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class GoalAssistantResponseDto {

    private String sessionId;
    private String step;
    private List<String> clarificationQuestions;
    private String goalName;
    private String goalType;
    private BigDecimal targetAmount;
    private String targetDate;
    private BigDecimal monthlyContribution;
    private String feasibilityAssessment;
    private Integer feasibilityPercent;
    private String createdGoalId;
    private String confirmationMessage;
}
