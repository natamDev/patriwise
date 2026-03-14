package com.finmate.application.dto;

import com.finmate.domain.model.Expense.ExpenseCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ExpenseDto {

    private UUID id;
    private UUID userId;
    private BigDecimal amount;
    private ExpenseCategory category;
    private String description;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;
}
