package com.finmate.domain.service;

import com.finmate.domain.model.Expense;
import com.finmate.domain.port.ExpenseRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Expense create(UUID userId, Expense expense) {
        expense.setUserId(userId);
        expense.setCreatedAt(LocalDateTime.now());
        return repository.save(expense);
    }

    public List<Expense> findByMonth(UUID userId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return repository.findByUserIdAndMonth(userId, start, end);
    }

    public Expense update(UUID userId, UUID expenseId, Expense patch) {
        Expense existing = repository.findById(expenseId)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Dépense introuvable."));

        if (patch.getAmount() != null) existing.setAmount(patch.getAmount());
        if (patch.getCategory() != null) existing.setCategory(patch.getCategory());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
        if (patch.getExpenseDate() != null) existing.setExpenseDate(patch.getExpenseDate());

        return repository.save(existing);
    }

    public void delete(UUID userId, UUID expenseId) {
        Expense existing = repository.findById(expenseId)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Dépense introuvable."));
        repository.delete(existing.getId());
    }
}
