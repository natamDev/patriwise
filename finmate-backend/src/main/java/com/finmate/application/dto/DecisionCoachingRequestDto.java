package com.finmate.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DecisionCoachingRequestDto {

    @NotBlank
    private String decisionContext;

    @NotBlank
    private String whyInvesting;

    @NotBlank
    private String investmentHorizon;

    @NotBlank
    private String riskTolerance;

    @NotBlank
    private String financialGoal;
}
