package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.UserBadge;
import com.finmate.domain.port.UserBadgeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserBadgeRepositoryImpl implements UserBadgeRepository {

    private final EntityManager em;

    public UserBadgeRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public UserBadge save(UserBadge ub) {
        UserBadgeEntity entity = toEntity(ub);
        if (ub.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        em.flush();
        return toDomain(entity);
    }

    @Override
    public List<UserBadge> findByUserId(UUID userId) {
        return UserBadgeEntity.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByUserIdAndBadgeId(UUID userId, String badgeId) {
        return UserBadgeEntity.existsByUserIdAndBadgeId(userId, badgeId);
    }

    private UserBadgeEntity toEntity(UserBadge ub) {
        UserBadgeEntity e = new UserBadgeEntity();
        e.setId(ub.getId());
        e.setUserId(ub.getUserId());
        e.setBadgeId(ub.getBadgeId());
        e.setUnlockedAt(ub.getUnlockedAt());
        return e;
    }

    private UserBadge toDomain(UserBadgeEntity e) {
        UserBadge ub = new UserBadge();
        ub.setId(e.getId());
        ub.setUserId(e.getUserId());
        ub.setBadgeId(e.getBadgeId());
        ub.setUnlockedAt(e.getUnlockedAt());
        return ub;
    }
}
