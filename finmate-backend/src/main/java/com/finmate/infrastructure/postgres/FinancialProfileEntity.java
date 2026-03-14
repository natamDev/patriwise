package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.FinancialProfile.EmploymentStatus;
import com.finmate.domain.model.FinancialProfile.FinancialExperienceLevel;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "financial_profiles")
@Getter
@Setter
@NoArgsConstructor
public class FinancialProfileEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "monthly_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyIncome;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false)
    private EmploymentStatus employmentStatus;

    @Column(nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_experience_level", nullable = false)
    private FinancialExperienceLevel financialExperienceLevel;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static Optional<FinancialProfileEntity> findByUserId(UUID userId) {
        return find("userId", userId).firstResultOptional();
    }

    public static boolean existsByUserId(UUID userId) {
        return count("userId", userId) > 0;
    }
}
