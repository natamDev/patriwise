package com.finmate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssistantRecommendation {

    private UUID id;
    private UUID userId;
    private RecommendationType recommendationType;
    private String message;
    private String suggestedAction;
    private LocalDateTime createdAt;

    public enum RecommendationType {
        COMPLETE_PROFILE,
        INCREASE_SAVINGS,
        REDUCE_EXPENSES,
        START_INVESTING,
        CONTRIBUTE_TO_GOALS,
        WELL_DONE
    }
}
