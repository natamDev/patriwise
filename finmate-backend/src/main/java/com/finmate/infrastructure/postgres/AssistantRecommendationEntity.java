package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.AssistantRecommendation.RecommendationType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assistant_recommendations")
@Getter
@Setter
@NoArgsConstructor
public class AssistantRecommendationEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_type", nullable = false)
    private RecommendationType recommendationType;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "suggested_action", nullable = false)
    private String suggestedAction;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static List<AssistantRecommendationEntity> findByUserId(UUID userId) {
        return find("userId order by createdAt desc", userId).list();
    }
}
