package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.FinancialProfile;
import com.finmate.domain.port.FinancialProfileRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class FinancialProfileRepositoryImpl implements FinancialProfileRepository {

    private final EntityManager em;

    public FinancialProfileRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public FinancialProfile save(FinancialProfile profile) {
        FinancialProfileEntity entity = toEntity(profile);
        if (profile.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        em.flush();
        return toDomain(entity);
    }

    @Override
    public Optional<FinancialProfile> findByUserId(UUID userId) {
        return FinancialProfileEntity.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return FinancialProfileEntity.existsByUserId(userId);
    }

    // --- Mappers ---

    private FinancialProfileEntity toEntity(FinancialProfile profile) {
        FinancialProfileEntity entity = new FinancialProfileEntity();
        entity.setId(profile.getId());
        entity.setUserId(profile.getUserId());
        entity.setMonthlyIncome(profile.getMonthlyIncome());
        entity.setEmploymentStatus(profile.getEmploymentStatus());
        entity.setAge(profile.getAge());
        entity.setFinancialExperienceLevel(profile.getFinancialExperienceLevel());
        entity.setCurrency(profile.getCurrency());
        entity.setCreatedAt(profile.getCreatedAt());
        entity.setUpdatedAt(profile.getUpdatedAt());
        return entity;
    }

    private FinancialProfile toDomain(FinancialProfileEntity entity) {
        FinancialProfile profile = new FinancialProfile();
        profile.setId(entity.getId());
        profile.setUserId(entity.getUserId());
        profile.setMonthlyIncome(entity.getMonthlyIncome());
        profile.setEmploymentStatus(entity.getEmploymentStatus());
        profile.setAge(entity.getAge());
        profile.setFinancialExperienceLevel(entity.getFinancialExperienceLevel());
        profile.setCurrency(entity.getCurrency());
        profile.setCreatedAt(entity.getCreatedAt());
        profile.setUpdatedAt(entity.getUpdatedAt());
        return profile;
    }
}
