package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.AssistantRecommendation;
import com.finmate.domain.port.AssistantRecommendationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AssistantRecommendationRepositoryImpl implements AssistantRecommendationRepository {

    private final EntityManager em;

    public AssistantRecommendationRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public AssistantRecommendation save(AssistantRecommendation r) {
        AssistantRecommendationEntity entity = toEntity(r);
        if (r.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        em.flush();
        return toDomain(entity);
    }

    @Override
    public List<AssistantRecommendation> findByUserId(UUID userId) {
        return AssistantRecommendationEntity.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    private AssistantRecommendationEntity toEntity(AssistantRecommendation r) {
        AssistantRecommendationEntity e = new AssistantRecommendationEntity();
        e.setId(r.getId());
        e.setUserId(r.getUserId());
        e.setRecommendationType(r.getRecommendationType());
        e.setMessage(r.getMessage());
        e.setSuggestedAction(r.getSuggestedAction());
        e.setCreatedAt(r.getCreatedAt());
        return e;
    }

    private AssistantRecommendation toDomain(AssistantRecommendationEntity e) {
        AssistantRecommendation r = new AssistantRecommendation();
        r.setId(e.getId());
        r.setUserId(e.getUserId());
        r.setRecommendationType(e.getRecommendationType());
        r.setMessage(e.getMessage());
        r.setSuggestedAction(e.getSuggestedAction());
        r.setCreatedAt(e.getCreatedAt());
        return r;
    }
}
