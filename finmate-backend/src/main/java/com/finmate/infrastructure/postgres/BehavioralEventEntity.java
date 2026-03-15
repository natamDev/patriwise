package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.BehavioralEvent;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "behavioral_events")
public class BehavioralEventEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "user_id", nullable = false)
    public UUID userId;

    @Column(name = "bias_type", nullable = false)
    public String biasType;

    @Column(name = "trigger_message", columnDefinition = "TEXT")
    public String triggerMessage;

    @Column(columnDefinition = "TEXT")
    public String explanation;

    @Column(name = "detected_at", nullable = false)
    public LocalDateTime detectedAt;

    public static List<BehavioralEventEntity> findByUserId(UUID userId) {
        return find("userId = ?1 order by detectedAt desc", userId).list();
    }

    public BehavioralEvent toDomain() {
        BehavioralEvent e = new BehavioralEvent();
        e.setId(id);
        e.setUserId(userId);
        e.setBiasType(biasType);
        e.setTriggerMessage(triggerMessage);
        e.setExplanation(explanation);
        e.setDetectedAt(detectedAt);
        return e;
    }

    public static BehavioralEventEntity fromDomain(BehavioralEvent e) {
        BehavioralEventEntity entity = new BehavioralEventEntity();
        entity.userId = e.getUserId();
        entity.biasType = e.getBiasType();
        entity.triggerMessage = e.getTriggerMessage();
        entity.explanation = e.getExplanation();
        entity.detectedAt = e.getDetectedAt();
        return entity;
    }
}
