package com.finmate.application.dto;

import com.finmate.domain.model.Expense.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UpdateExpenseDto {

    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    private ExpenseCategory category;

    private String description;

    private LocalDate expenseDate;
}
