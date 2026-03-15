package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FinancialProjectionDto {

    private double monthlyInvestment;
    private int horizonYears;
    private String currency;
    private ScenarioDto conservative;
    private ScenarioDto moderate;
    private ScenarioDto optimistic;
    private String explanation;

    @Data
    @NoArgsConstructor
    public static class ScenarioDto {
        private String label;
        private double returnPct;
        private double capitalInvested;
        private double capitalFinal;
        private double interestGain;
    }
}
