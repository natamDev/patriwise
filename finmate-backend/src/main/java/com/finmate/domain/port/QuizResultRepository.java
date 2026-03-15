package com.finmate.domain.port;

import com.finmate.domain.model.QuizResult;

import java.util.List;
import java.util.UUID;

public interface QuizResultRepository {
    QuizResult save(QuizResult result);
    List<QuizResult> findByUserId(UUID userId);
    boolean existsByUserIdAndQuizId(UUID userId, UUID quizId);
}
