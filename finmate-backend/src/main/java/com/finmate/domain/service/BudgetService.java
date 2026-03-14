package com.finmate.domain.service;

import com.finmate.domain.model.Expense;
import com.finmate.domain.model.Expense.ExpenseCategory;
import com.finmate.domain.model.FinancialProfile;
import com.finmate.domain.port.ExpenseRepository;
import com.finmate.domain.port.FinancialProfileRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class BudgetService {

    private final FinancialProfileRepository profileRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(FinancialProfileRepository profileRepository, ExpenseRepository expenseRepository) {
        this.profileRepository = profileRepository;
        this.expenseRepository = expenseRepository;
    }

    public BudgetSummary computeSummary(UUID userId, YearMonth month) {
        BigDecimal income = profileRepository.findByUserId(userId)
                .map(FinancialProfile::getMonthlyIncome)
                .orElse(BigDecimal.ZERO);

        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<Expense> expenses = expenseRepository.findByUserIdAndMonth(userId, start, end);

        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = income.subtract(totalExpenses);

        double savingsRate = income.compareTo(BigDecimal.ZERO) > 0
                ? remaining.divide(income, 4, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        Map<ExpenseCategory, BigDecimal> byCategory = new EnumMap<>(ExpenseCategory.class);
        for (Expense e : expenses) {
            byCategory.merge(e.getCategory(), e.getAmount(), BigDecimal::add);
        }

        return new BudgetSummary(income, totalExpenses, remaining, savingsRate, byCategory, month.toString());
    }

    public List<MonthTrend> computeTrend(UUID userId, int months) {
        BigDecimal income = profileRepository.findByUserId(userId)
                .map(FinancialProfile::getMonthlyIncome)
                .orElse(BigDecimal.ZERO);

        YearMonth current = YearMonth.now();
        List<MonthTrend> trend = new java.util.ArrayList<>();
        for (int i = months - 1; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();
            List<Expense> expenses = expenseRepository.findByUserIdAndMonth(userId, start, end);
            BigDecimal totalExpenses = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            trend.add(new MonthTrend(ym.toString(), income, totalExpenses));
        }
        return trend;
    }

    public record BudgetSummary(
            BigDecimal monthlyIncome,
            BigDecimal totalExpenses,
            BigDecimal remainingIncome,
            double savingsRate,
            Map<ExpenseCategory, BigDecimal> expensesByCategory,
            String month
    ) {}

    public record MonthTrend(
            String month,
            BigDecimal income,
            BigDecimal expenses
    ) {}
}
