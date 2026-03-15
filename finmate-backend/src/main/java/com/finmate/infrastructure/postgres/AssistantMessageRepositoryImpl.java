package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.AssistantMessage;
import com.finmate.domain.port.AssistantMessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AssistantMessageRepositoryImpl implements AssistantMessageRepository {

    private final EntityManager em;

    public AssistantMessageRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public AssistantMessage save(AssistantMessage message) {
        AssistantMessageEntity entity = toEntity(message);
        if (message.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        em.flush();
        return toDomain(entity);
    }

    @Override
    public List<AssistantMessage> findByConversationId(UUID conversationId) {
        return AssistantMessageEntity.findByConversationId(conversationId).stream().map(this::toDomain).toList();
    }

    private AssistantMessageEntity toEntity(AssistantMessage m) {
        AssistantMessageEntity e = new AssistantMessageEntity();
        e.setId(m.getId());
        e.setConversationId(m.getConversationId());
        e.setRole(m.getRole());
        e.setContent(m.getContent());
        e.setCreatedAt(m.getCreatedAt());
        return e;
    }

    private AssistantMessage toDomain(AssistantMessageEntity e) {
        AssistantMessage m = new AssistantMessage();
        m.setId(e.getId());
        m.setConversationId(e.getConversationId());
        m.setRole(e.getRole());
        m.setContent(e.getContent());
        m.setCreatedAt(e.getCreatedAt());
        return m;
    }
}
