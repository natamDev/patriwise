package com.finmate.infrastructure.postgres;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_badges")
@Getter
@Setter
@NoArgsConstructor
public class UserBadgeEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "badge_id", nullable = false)
    private String badgeId;

    @Column(name = "unlocked_at", nullable = false)
    private LocalDateTime unlockedAt;

    public static List<UserBadgeEntity> findByUserId(UUID userId) {
        return find("userId", userId).list();
    }

    public static boolean existsByUserIdAndBadgeId(UUID userId, String badgeId) {
        return count("userId = ?1 and badgeId = ?2", userId, badgeId) > 0;
    }
}
