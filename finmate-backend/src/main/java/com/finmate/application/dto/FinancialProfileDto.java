package com.finmate.application.dto;

import com.finmate.domain.model.FinancialProfile.EmploymentStatus;
import com.finmate.domain.model.FinancialProfile.FinancialExperienceLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class FinancialProfileDto {

    private UUID id;
    private UUID userId;
    private BigDecimal monthlyIncome;
    private EmploymentStatus employmentStatus;
    private int age;
    private FinancialExperienceLevel financialExperienceLevel;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
