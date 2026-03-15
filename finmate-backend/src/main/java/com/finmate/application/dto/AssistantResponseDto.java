package com.finmate.application.dto;

import com.finmate.domain.model.ConceptCard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AssistantResponseDto {

    private UUID conversationId;
    private String reply;
    private ConceptCard conceptCard; // null si aucun concept détecté
}
