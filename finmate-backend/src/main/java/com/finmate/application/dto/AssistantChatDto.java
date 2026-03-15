package com.finmate.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssistantChatDto {

    private UUID conversationId;

    @NotBlank
    private String message;
}
