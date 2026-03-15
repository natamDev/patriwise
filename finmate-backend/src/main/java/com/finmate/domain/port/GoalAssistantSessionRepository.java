package com.finmate.domain.port;

import com.finmate.domain.model.GoalAssistantSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalAssistantSessionRepository {
    GoalAssistantSession save(GoalAssistantSession session);
    Optional<GoalAssistantSession> findById(UUID id);
    List<GoalAssistantSession> findByUserId(UUID userId);
}
