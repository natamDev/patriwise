package com.finmate.domain.service;

import com.finmate.domain.model.Expense;
import com.finmate.domain.model.FinancialProfile;
import com.finmate.domain.port.ExpenseRepository;
import com.finmate.domain.port.FinancialProfileRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class FinancialScoreService {

    private final FinancialProfileRepository profileRepository;
    private final ExpenseRepository expenseRepository;

    public FinancialScoreService(FinancialProfileRepository profileRepository, ExpenseRepository expenseRepository) {
        this.profileRepository = profileRepository;
        this.expenseRepository = expenseRepository;
    }

    public FinancialScore compute(UUID userId) {
        FinancialProfile profile = profileRepository.findByUserId(userId).orElse(null);
        BigDecimal income = profile != null ? profile.getMonthlyIncome() : BigDecimal.ZERO;

        YearMonth current = YearMonth.now();
        LocalDate start = current.atDay(1);
        LocalDate end = current.atEndOfMonth();
        List<Expense> expenses = expenseRepository.findByUserIdAndMonth(userId, start, end);
        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double savingsRateScore = computeSavingsRateScore(income, totalExpenses);
        double expenseControlScore = computeExpenseControlScore(income, totalExpenses);
        double financialStabilityScore = computeStabilityScore(profile);
        double goalProgressScore = 0.0; // EPIC 2 — non implémenté

        int total = (int) Math.round(savingsRateScore + expenseControlScore + financialStabilityScore + goalProgressScore);
        total = Math.max(0, Math.min(100, total));

        String label = scoreLabel(total);
        String explanation = explanation(total, savingsRateScore, expenseControlScore, financialStabilityScore);

        return new FinancialScore(total, label, explanation, savingsRateScore, expenseControlScore, financialStabilityScore, goalProgressScore);
    }

    private double computeSavingsRateScore(BigDecimal income, BigDecimal expenses) {
        if (income.compareTo(BigDecimal.ZERO) <= 0) return 0.0;
        BigDecimal remaining = income.subtract(expenses);
        double rate = remaining.divide(income, 4, RoundingMode.HALF_UP).doubleValue();
        rate = Math.max(0.0, Math.min(1.0, rate));
        return rate * 50.0;
    }

    private double computeExpenseControlScore(BigDecimal income, BigDecimal expenses) {
        if (income.compareTo(BigDecimal.ZERO) <= 0) return 0.0;
        double ratio = expenses.divide(income, 4, RoundingMode.HALF_UP).doubleValue();
        double control;
        if (ratio <= 0.5) control = 1.0;
        else if (ratio <= 0.7) control = 0.7;
        else if (ratio <= 0.9) control = 0.4;
        else control = 0.0;
        return control * 20.0;
    }

    private double computeStabilityScore(FinancialProfile profile) {
        if (profile == null) return 0.0;
        double stability = switch (profile.getEmploymentStatus()) {
            case EMPLOYEE -> 1.0;
            case FREELANCER -> 0.7;
            case STUDENT -> 0.5;
            case UNEMPLOYED -> 0.0;
        };
        return stability * 10.0;
    }

    private String scoreLabel(int score) {
        if (score >= 70) return "healthy";
        if (score >= 40) return "improving";
        return "poor";
    }

    private String explanation(int score, double savings, double expenses, double stability) {
        if (score >= 70) return "Votre situation financière est solide. Continuez sur cette lancée !";
        if (score >= 40) return "Vous progressez bien. Réduire vos dépenses améliorera votre score.";
        return "Votre situation financière nécessite attention. Commencez par augmenter votre épargne.";
    }

    public record FinancialScore(
            int score,
            String label,
            String explanation,
            double savingsRateScore,
            double expenseControlScore,
            double financialStabilityScore,
            double goalProgressScore
    ) {}
}
