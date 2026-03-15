package com.finmate.application.dto;

import com.finmate.domain.model.AssistantRecommendation.RecommendationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class AssistantRecommendationDto {
    private UUID id;
    private RecommendationType recommendationType;
    private String message;
    private String suggestedAction;
    private LocalDateTime createdAt;
}
