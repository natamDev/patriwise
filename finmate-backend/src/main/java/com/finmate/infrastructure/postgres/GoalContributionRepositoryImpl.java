package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.GoalContribution;
import com.finmate.domain.port.GoalContributionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class GoalContributionRepositoryImpl implements GoalContributionRepository {

    private final EntityManager em;

    public GoalContributionRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public GoalContribution save(GoalContribution contribution) {
        GoalContributionEntity entity = toEntity(contribution);
        em.persist(entity);
        em.flush();
        return toDomain(entity);
    }

    @Override
    public List<GoalContribution> findByGoalId(UUID goalId) {
        return GoalContributionEntity.findByGoalId(goalId).stream().map(this::toDomain).toList();
    }

    @Override
    public BigDecimal sumByGoalId(UUID goalId) {
        return GoalContributionEntity.sumByGoalId(goalId);
    }

    private GoalContributionEntity toEntity(GoalContribution c) {
        GoalContributionEntity e = new GoalContributionEntity();
        e.setGoalId(c.getGoalId());
        e.setAmount(c.getAmount());
        e.setContributionDate(c.getContributionDate());
        return e;
    }

    private GoalContribution toDomain(GoalContributionEntity e) {
        GoalContribution c = new GoalContribution();
        c.setId(e.getId());
        c.setGoalId(e.getGoalId());
        c.setAmount(e.getAmount());
        c.setContributionDate(e.getContributionDate());
        return c;
    }
}
