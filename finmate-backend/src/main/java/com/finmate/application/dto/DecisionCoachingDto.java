package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class DecisionCoachingDto {

    private UUID sessionId;
    private String recommendation;
    private String decisionContext;
    private String whyInvesting;
    private String investmentHorizon;
    private String riskTolerance;
    private String financialGoal;
}
