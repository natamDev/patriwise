package com.finmate.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InvestmentSimulationRequestDto {

    @Positive
    private double monthlyInvestment;

    @Min(0)
    @Max(50)
    private double expectedReturn;

    @Min(1)
    @Max(50)
    private int horizonYears;
}
