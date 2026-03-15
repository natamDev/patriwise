package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.SavingStreak;
import com.finmate.domain.port.SavingStreakRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SavingStreakRepositoryImpl implements SavingStreakRepository {

    private final EntityManager em;

    public SavingStreakRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<SavingStreak> findByUserId(UUID userId) {
        return SavingStreakEntity.findByUserId(userId).map(this::toDomain);
    }

    @Override
    @Transactional
    public SavingStreak save(SavingStreak streak) {
        SavingStreakEntity entity = SavingStreakEntity.findByUserId(streak.getUserId())
                .orElseGet(() -> {
                    SavingStreakEntity e = new SavingStreakEntity();
                    e.setUserId(streak.getUserId());
                    return e;
                });
        entity.setCurrentStreak(streak.getCurrentStreak());
        entity.setLongestStreak(streak.getLongestStreak());

        if (em.contains(entity)) {
            em.merge(entity);
        } else {
            em.persist(entity);
        }
        em.flush();
        return toDomain(entity);
    }

    private SavingStreak toDomain(SavingStreakEntity entity) {
        return new SavingStreak(entity.getUserId(), entity.getCurrentStreak(), entity.getLongestStreak());
    }
}
