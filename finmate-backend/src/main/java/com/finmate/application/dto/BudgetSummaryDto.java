package com.finmate.application.dto;

import com.finmate.domain.model.Expense.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
public class BudgetSummaryDto {

    private BigDecimal monthlyIncome;
    private BigDecimal totalExpenses;
    private BigDecimal remainingIncome;
    private double savingsRate;
    private Map<ExpenseCategory, BigDecimal> expensesByCategory;
    private String month;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthTrendDto {
        private String month;
        private BigDecimal income;
        private BigDecimal expenses;
    }
}
