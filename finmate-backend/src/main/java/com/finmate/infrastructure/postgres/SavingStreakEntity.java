package com.finmate.infrastructure.postgres;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "saving_streaks")
@Getter
@Setter
@NoArgsConstructor
public class SavingStreakEntity extends PanacheEntityBase {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak;

    @Column(name = "longest_streak", nullable = false)
    private int longestStreak;

    public static Optional<SavingStreakEntity> findByUserId(UUID userId) {
        return find("userId", userId).firstResultOptional();
    }
}
