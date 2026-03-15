package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.BehavioralEvent;
import com.finmate.domain.port.BehavioralEventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BehavioralEventRepositoryImpl implements BehavioralEventRepository {

    @Override
    @Transactional
    public BehavioralEvent save(BehavioralEvent event) {
        BehavioralEventEntity entity = BehavioralEventEntity.fromDomain(event);
        entity.persist();
        return entity.toDomain();
    }

    @Override
    public List<BehavioralEvent> findByUserId(UUID userId) {
        return BehavioralEventEntity.findByUserId(userId).stream()
                .map(BehavioralEventEntity::toDomain)
                .toList();
    }
}
