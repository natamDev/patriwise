package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InvestmentSimulationDto {

    private double monthlyInvestment;
    private double expectedReturn;
    private int horizonYears;
    private double capitalInvested;
    private double capitalFinal;
    private double interestGain;
    private String explanation;
}
