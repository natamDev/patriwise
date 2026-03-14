package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.Goal;
import com.finmate.domain.port.GoalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GoalRepositoryImpl implements GoalRepository {

    private final EntityManager em;

    public GoalRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public Goal save(Goal goal) {
        GoalEntity entity = toEntity(goal);
        if (goal.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        em.flush();
        return toDomain(entity);
    }

    @Override
    public List<Goal> findByUserId(UUID userId) {
        return GoalEntity.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Goal> findById(UUID id) {
        return Optional.ofNullable(em.find(GoalEntity.class, id)).map(this::toDomain);
    }

    @Override
    @Transactional
    public void delete(Goal goal) {
        GoalEntity entity = em.find(GoalEntity.class, goal.getId());
        if (entity != null) {
            em.remove(entity);
        }
    }

    private GoalEntity toEntity(Goal g) {
        GoalEntity e = new GoalEntity();
        e.setId(g.getId());
        e.setUserId(g.getUserId());
        e.setGoalName(g.getGoalName());
        e.setGoalType(g.getGoalType());
        e.setTargetAmount(g.getTargetAmount());
        e.setTargetDate(g.getTargetDate());
        e.setMonthlyContribution(g.getMonthlyContribution());
        e.setCreatedAt(g.getCreatedAt());
        return e;
    }

    private Goal toDomain(GoalEntity e) {
        Goal g = new Goal();
        g.setId(e.getId());
        g.setUserId(e.getUserId());
        g.setGoalName(e.getGoalName());
        g.setGoalType(e.getGoalType());
        g.setTargetAmount(e.getTargetAmount());
        g.setTargetDate(e.getTargetDate());
        g.setMonthlyContribution(e.getMonthlyContribution());
        g.setCreatedAt(e.getCreatedAt());
        return g;
    }
}
