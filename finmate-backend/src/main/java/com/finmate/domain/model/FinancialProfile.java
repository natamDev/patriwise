package com.finmate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinancialProfile {

    private UUID id;
    private UUID userId;
    private BigDecimal monthlyIncome;
    private EmploymentStatus employmentStatus;
    private int age;
    private FinancialExperienceLevel financialExperienceLevel;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum EmploymentStatus {
        STUDENT, EMPLOYEE, FREELANCER, UNEMPLOYED
    }

    public enum FinancialExperienceLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}
