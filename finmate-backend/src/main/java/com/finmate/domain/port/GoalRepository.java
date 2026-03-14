package com.finmate.domain.port;

import com.finmate.domain.model.Goal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository {
    Goal save(Goal goal);
    List<Goal> findByUserId(UUID userId);
    Optional<Goal> findById(UUID id);
    void delete(Goal goal);
}
