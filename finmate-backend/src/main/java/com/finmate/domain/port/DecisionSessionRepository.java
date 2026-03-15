package com.finmate.domain.port;

import com.finmate.domain.model.DecisionSession;

import java.util.List;
import java.util.UUID;

public interface DecisionSessionRepository {
    DecisionSession save(DecisionSession session);
    List<DecisionSession> findByUserId(UUID userId);
}
