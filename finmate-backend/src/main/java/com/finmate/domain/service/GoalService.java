package com.finmate.domain.service;

import com.finmate.domain.model.Goal;
import com.finmate.domain.port.GoalRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class GoalService {

    private final GoalRepository repository;

    public GoalService(GoalRepository repository) {
        this.repository = repository;
    }

    public Goal create(Goal goal, UUID userId) {
        if (goal.getTargetAmount() != null && goal.getTargetAmount().signum() <= 0) {
            throw new IllegalArgumentException("Le montant cible doit être supérieur à zéro.");
        }
        if (goal.getTargetDate() != null && goal.getTargetDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La date cible ne peut pas être dans le passé.");
        }
        goal.setUserId(userId);
        goal.setCreatedAt(LocalDateTime.now());
        return repository.save(goal);
    }

    public List<Goal> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    public Goal update(UUID goalId, UUID userId, Goal patch) {
        Goal existing = repository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Objectif introuvable."));
        if (!existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Objectif introuvable.");
        }
        if (patch.getGoalName() != null) existing.setGoalName(patch.getGoalName());
        if (patch.getGoalType() != null) existing.setGoalType(patch.getGoalType());
        if (patch.getTargetAmount() != null) {
            if (patch.getTargetAmount().signum() <= 0) {
                throw new IllegalArgumentException("Le montant cible doit être supérieur à zéro.");
            }
            existing.setTargetAmount(patch.getTargetAmount());
        }
        if (patch.getTargetDate() != null) {
            if (patch.getTargetDate().isBefore(java.time.LocalDate.now())) {
                throw new IllegalArgumentException("La date cible ne peut pas être dans le passé.");
            }
            existing.setTargetDate(patch.getTargetDate());
        }
        if (patch.getMonthlyContribution() != null) existing.setMonthlyContribution(patch.getMonthlyContribution());
        return repository.save(existing);
    }

    public void delete(UUID goalId, UUID userId) {
        Goal existing = repository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Objectif introuvable."));
        if (!existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Objectif introuvable.");
        }
        repository.delete(existing);
    }
}
