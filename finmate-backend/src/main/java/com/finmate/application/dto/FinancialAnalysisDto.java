package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class FinancialAnalysisDto {

    private String analysis;
    private Map<String, Double> expenseByCategory;
    private double savingCapacity;
    private List<String> spendingAlerts;
}
