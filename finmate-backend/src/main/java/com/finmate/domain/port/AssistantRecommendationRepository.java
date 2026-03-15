package com.finmate.domain.port;

import com.finmate.domain.model.AssistantRecommendation;

import java.util.List;
import java.util.UUID;

public interface AssistantRecommendationRepository {

    AssistantRecommendation save(AssistantRecommendation recommendation);

    List<AssistantRecommendation> findByUserId(UUID userId);
}
