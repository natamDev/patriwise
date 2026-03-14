package com.finmate.application.dto;

import com.finmate.domain.model.FinancialProfile.EmploymentStatus;
import com.finmate.domain.model.FinancialProfile.FinancialExperienceLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class UpdateFinancialProfileDto {

    @DecimalMin(value = "0.0")
    private BigDecimal monthlyIncome;

    private EmploymentStatus employmentStatus;

    @Min(16)
    @Max(120)
    private int age;

    private FinancialExperienceLevel financialExperienceLevel;

    private String currency;
}
