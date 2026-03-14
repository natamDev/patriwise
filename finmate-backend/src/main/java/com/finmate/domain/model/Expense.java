package com.finmate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expense {

    private UUID id;
    private UUID userId;
    private BigDecimal amount;
    private ExpenseCategory category;
    private String description;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;

    public enum ExpenseCategory {
        HOUSING, TRANSPORT, FOOD, SUBSCRIPTIONS, ENTERTAINMENT, SHOPPING, HEALTH, OTHER
    }
}
