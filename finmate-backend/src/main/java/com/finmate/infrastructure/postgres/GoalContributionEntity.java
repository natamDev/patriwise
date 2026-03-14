package com.finmate.infrastructure.postgres;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "goal_contributions")
@Getter
@Setter
@NoArgsConstructor
public class GoalContributionEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "goal_id", nullable = false)
    private UUID goalId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "contribution_date", nullable = false)
    private LocalDate contributionDate;

    public static List<GoalContributionEntity> findByGoalId(UUID goalId) {
        return find("goalId", goalId).list();
    }

    public static BigDecimal sumByGoalId(UUID goalId) {
        return (BigDecimal) getEntityManager()
                .createQuery("SELECT COALESCE(SUM(c.amount), 0) FROM GoalContributionEntity c WHERE c.goalId = :goalId")
                .setParameter("goalId", goalId)
                .getSingleResult();
    }
}
