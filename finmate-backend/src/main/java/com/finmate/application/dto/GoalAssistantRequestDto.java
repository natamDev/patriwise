package com.finmate.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class GoalAssistantRequestDto {

    @NotBlank
    private String step;

    private String userIntent;
    private String sessionId;
    private String userAnswers;
    private String goalName;
    private String goalType;
    private BigDecimal targetAmount;
    private String targetDate;
    private BigDecimal monthlyContribution;
}
