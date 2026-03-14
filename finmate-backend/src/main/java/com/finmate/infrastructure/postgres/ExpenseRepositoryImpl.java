package com.finmate.infrastructure.postgres;

import com.finmate.domain.model.Expense;
import com.finmate.domain.port.ExpenseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ExpenseRepositoryImpl implements ExpenseRepository {

    private final EntityManager em;

    public ExpenseRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public Expense save(Expense expense) {
        ExpenseEntity entity = toEntity(expense);
        if (expense.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        em.flush();
        return toDomain(entity);
    }

    @Override
    public Optional<Expense> findById(UUID id) {
        return Optional.ofNullable(em.find(ExpenseEntity.class, id)).map(this::toDomain);
    }

    @Override
    public List<Expense> findByUserIdAndMonth(UUID userId, LocalDate start, LocalDate end) {
        return ExpenseEntity.findByUserIdAndMonth(userId, start, end).stream()
                .map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ExpenseEntity entity = em.find(ExpenseEntity.class, id);
        if (entity != null) {
            em.remove(entity);
        }
    }

    // --- Mappers ---

    private ExpenseEntity toEntity(Expense e) {
        ExpenseEntity entity = new ExpenseEntity();
        entity.setId(e.getId());
        entity.setUserId(e.getUserId());
        entity.setAmount(e.getAmount());
        entity.setCategory(e.getCategory());
        entity.setDescription(e.getDescription());
        entity.setExpenseDate(e.getExpenseDate());
        entity.setCreatedAt(e.getCreatedAt());
        return entity;
    }

    private Expense toDomain(ExpenseEntity entity) {
        Expense e = new Expense();
        e.setId(entity.getId());
        e.setUserId(entity.getUserId());
        e.setAmount(entity.getAmount());
        e.setCategory(entity.getCategory());
        e.setDescription(entity.getDescription());
        e.setExpenseDate(entity.getExpenseDate());
        e.setCreatedAt(entity.getCreatedAt());
        return e;
    }
}
