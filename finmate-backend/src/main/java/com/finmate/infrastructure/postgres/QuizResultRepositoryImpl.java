package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.QuizResult;
import com.finmate.domain.port.QuizResultRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class QuizResultRepositoryImpl implements QuizResultRepository {

    private final EntityManager em;

    public QuizResultRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public QuizResult save(QuizResult r) {
        QuizResultEntity entity = toEntity(r);
        if (r.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        em.flush();
        return toDomain(entity);
    }

    @Override
    public List<QuizResult> findByUserId(UUID userId) {
        return QuizResultEntity.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByUserIdAndQuizId(UUID userId, UUID quizId) {
        return QuizResultEntity.existsByUserIdAndQuizId(userId, quizId);
    }

    private QuizResultEntity toEntity(QuizResult r) {
        QuizResultEntity e = new QuizResultEntity();
        e.setId(r.getId());
        e.setUserId(r.getUserId());
        e.setQuizId(r.getQuizId());
        e.setScore(r.getScore());
        e.setAnsweredAt(r.getAnsweredAt());
        return e;
    }

    private QuizResult toDomain(QuizResultEntity e) {
        QuizResult r = new QuizResult();
        r.setId(e.getId());
        r.setUserId(e.getUserId());
        r.setQuizId(e.getQuizId());
        r.setScore(e.getScore());
        r.setAnsweredAt(e.getAnsweredAt());
        return r;
    }
}
