package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.DecisionSession;
import com.finmate.domain.port.DecisionSessionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DecisionSessionRepositoryImpl implements DecisionSessionRepository {

    @Override
    @Transactional
    public DecisionSession save(DecisionSession session) {
        DecisionSessionEntity entity = DecisionSessionEntity.fromDomain(session);
        entity.persist();
        return entity.toDomain();
    }

    @Override
    public List<DecisionSession> findByUserId(UUID userId) {
        return DecisionSessionEntity.findByUserId(userId).stream()
                .map(DecisionSessionEntity::toDomain)
                .toList();
    }
}
