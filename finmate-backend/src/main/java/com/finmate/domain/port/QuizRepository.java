package com.finmate.domain.port;

import com.finmate.domain.model.Quiz;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizRepository {
    Quiz save(Quiz quiz);
    List<Quiz> findAll();
    Optional<Quiz> findById(UUID id);
    long count();
}
