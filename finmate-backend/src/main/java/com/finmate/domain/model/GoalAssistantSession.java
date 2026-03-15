package com.finmate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalAssistantSession {

    private UUID id;
    private UUID userId;
    private String userIntent;
    private String clarificationQuestions;
    private String userAnswers;
    private String proposedGoalName;
    private GoalType proposedGoalType;
    private BigDecimal proposedTargetAmount;
    private LocalDate proposedTargetDate;
    private BigDecimal proposedMonthlyContribution;
    private String feasibilityAssessment;
    private Integer feasibilityPercent;
    private Status status;
    private UUID createdGoalId;
    private LocalDateTime createdAt;

    public enum Status {
        AWAITING_CLARIFICATION, PROPOSAL_SENT, CONFIRMED, CANCELLED
    }

    public enum GoalType {
        TRAVEL, EMERGENCY_FUND, INVESTMENT, PURCHASE, OTHER
    }
}
