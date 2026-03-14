package com.finmate.domain.port;

import com.finmate.domain.model.Expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository {

    Expense save(Expense expense);

    Optional<Expense> findById(UUID id);

    List<Expense> findByUserIdAndMonth(UUID userId, LocalDate monthStart, LocalDate monthEnd);

    void delete(UUID id);
}
