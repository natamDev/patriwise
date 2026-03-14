package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.Goal.GoalType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
public class GoalEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "goal_name", nullable = false)
    private String goalName;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false)
    private GoalType goalType;

    @Column(name = "target_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "monthly_contribution", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyContribution;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static List<GoalEntity> findByUserId(UUID userId) {
        return find("userId", userId).list();
    }
}
