package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.Quiz;
import com.finmate.domain.port.QuizRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class QuizRepositoryImpl implements QuizRepository {

    private final EntityManager em;

    public QuizRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public Quiz save(Quiz quiz) {
        QuizEntity entity = toEntity(quiz);
        if (quiz.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        em.flush();
        return toDomain(entity);
    }

    @Override
    public List<Quiz> findAll() {
        return QuizEntity.findAllQuizzes().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Quiz> findById(UUID id) {
        return Optional.ofNullable(em.find(QuizEntity.class, id)).map(this::toDomain);
    }

    @Override
    public long count() {
        return QuizEntity.count();
    }

    private QuizEntity toEntity(Quiz q) {
        QuizEntity e = new QuizEntity();
        e.setId(q.getId());
        e.setQuestion(q.getQuestion());
        e.setOption0(q.getOptions().get(0));
        e.setOption1(q.getOptions().get(1));
        e.setOption2(q.getOptions().get(2));
        e.setOption3(q.getOptions().get(3));
        e.setCorrectAnswer(q.getCorrectAnswer());
        e.setExplanation(q.getExplanation());
        return e;
    }

    private Quiz toDomain(QuizEntity e) {
        return new Quiz(
                e.getId(),
                e.getQuestion(),
                List.of(e.getOption0(), e.getOption1(), e.getOption2(), e.getOption3()),
                e.getCorrectAnswer(),
                e.getExplanation()
        );
    }
}
