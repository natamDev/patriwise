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
@Table(name = "quiz_results")
@Getter
@Setter
@NoArgsConstructor
public class QuizResultEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "quiz_id", nullable = false)
    private UUID quizId;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt;

    public static List<QuizResultEntity> findByUserId(UUID userId) {
        return find("userId", userId).list();
    }

    public static boolean existsByUserIdAndQuizId(UUID userId, UUID quizId) {
        return count("userId = ?1 and quizId = ?2", userId, quizId) > 0;
    }
}
