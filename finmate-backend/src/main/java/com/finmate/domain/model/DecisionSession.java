package com.finmate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DecisionSession {

    private UUID id;
    private UUID userId;
    private String decisionContext;
    private String whyInvesting;
    private String investmentHorizon;
    private String riskTolerance;
    private String financialGoal;
    private String recommendation;
    private LocalDateTime createdAt;
}
