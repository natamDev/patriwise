package com.finmate.domain.port;

import com.finmate.domain.model.AssistantMessage;

import java.util.List;
import java.util.UUID;

public interface AssistantMessageRepository {

    AssistantMessage save(AssistantMessage message);

    List<AssistantMessage> findByConversationId(UUID conversationId);
}
