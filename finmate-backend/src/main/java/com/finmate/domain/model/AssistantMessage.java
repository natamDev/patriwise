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
public class AssistantMessage {

    private UUID id;
    private UUID conversationId;
    private String role; // "user" or "assistant"
    private String content;
    private LocalDateTime createdAt;
}
