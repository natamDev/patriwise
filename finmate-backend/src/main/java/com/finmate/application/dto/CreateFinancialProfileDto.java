package com.finmate.application.dto;

import com.finmate.domain.model.FinancialProfile.EmploymentStatus;
import com.finmate.domain.model.FinancialProfile.FinancialExperienceLevel;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CreateFinancialProfileDto {

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal monthlyIncome;

    @NotNull
    private EmploymentStatus employmentStatus;

    @Min(16)
    @Max(120)
    private int age;

    @NotNull
    private FinancialExperienceLevel financialExperienceLevel;

    @NotBlank
    private String currency;
}
