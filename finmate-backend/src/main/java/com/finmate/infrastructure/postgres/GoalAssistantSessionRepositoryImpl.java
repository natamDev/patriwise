package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.GoalAssistantSession;
import com.finmate.domain.port.GoalAssistantSessionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GoalAssistantSessionRepositoryImpl implements GoalAssistantSessionRepository {

    private final EntityManager em;

    public GoalAssistantSessionRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public GoalAssistantSession save(GoalAssistantSession session) {
        GoalAssistantSessionEntity entity = GoalAssistantSessionEntity.fromDomain(session);
        if (session.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        em.flush();
        return entity.toDomain();
    }

    @Override
    public Optional<GoalAssistantSession> findById(UUID id) {
        GoalAssistantSessionEntity entity = GoalAssistantSessionEntity.findById(id);
        return Optional.ofNullable(entity).map(GoalAssistantSessionEntity::toDomain);
    }

    @Override
    public List<GoalAssistantSession> findByUserId(UUID userId) {
        return GoalAssistantSessionEntity.findByUserId(userId).stream()
                .map(GoalAssistantSessionEntity::toDomain)
                .toList();
    }
}
