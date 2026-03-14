package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FinancialScoreDto {
    private int score;
    private String label;
    private String explanation;
    private double savingsRateScore;
    private double expenseControlScore;
    private double financialStabilityScore;
    private double goalProgressScore;
}
