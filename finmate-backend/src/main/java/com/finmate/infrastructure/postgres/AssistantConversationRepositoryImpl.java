package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.AssistantConversation;
import com.finmate.domain.port.AssistantConversationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AssistantConversationRepositoryImpl implements AssistantConversationRepository {

    private final EntityManager em;

    public AssistantConversationRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public AssistantConversation save(AssistantConversation conversation) {
        AssistantConversationEntity entity = toEntity(conversation);
        if (conversation.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        em.flush();
        return toDomain(entity);
    }

    @Override
    public Optional<AssistantConversation> findById(UUID id) {
        return Optional.ofNullable(em.find(AssistantConversationEntity.class, id)).map(this::toDomain);
    }

    @Override
    public List<AssistantConversation> findByUserId(UUID userId) {
        return AssistantConversationEntity.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    private AssistantConversationEntity toEntity(AssistantConversation c) {
        AssistantConversationEntity e = new AssistantConversationEntity();
        e.setId(c.getId());
        e.setUserId(c.getUserId());
        e.setCreatedAt(c.getCreatedAt());
        return e;
    }

    private AssistantConversation toDomain(AssistantConversationEntity e) {
        AssistantConversation c = new AssistantConversation();
        c.setId(e.getId());
        c.setUserId(e.getUserId());
        c.setCreatedAt(e.getCreatedAt());
        return c;
    }
}
