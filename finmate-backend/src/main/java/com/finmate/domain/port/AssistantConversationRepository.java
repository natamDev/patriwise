package com.finmate.domain.port;

import com.finmate.domain.model.AssistantConversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssistantConversationRepository {

    AssistantConversation save(AssistantConversation conversation);

    Optional<AssistantConversation> findById(UUID id);

    List<AssistantConversation> findByUserId(UUID userId);
}
