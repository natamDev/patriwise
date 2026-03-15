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
public class QuizResult {
    private UUID id;
    private UUID userId;
    private UUID quizId;
    private int score; // 1 = correct, 0 = wrong
    private LocalDateTime answeredAt;
}
