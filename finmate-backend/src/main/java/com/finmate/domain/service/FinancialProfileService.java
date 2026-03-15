package com.finmate.domain.service;

import com.finmate.domain.exception.ConflictException;
import com.finmate.domain.exception.ResourceNotFoundException;
import com.finmate.domain.model.FinancialProfile;
import com.finmate.domain.port.FinancialProfileRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class FinancialProfileService {

    private final FinancialProfileRepository repository;

    public FinancialProfileService(FinancialProfileRepository repository) {
        this.repository = repository;
    }

    public FinancialProfile create(FinancialProfile profile) {
        if (repository.existsByUserId(profile.getUserId())) {
            throw new ConflictException("Un profil financier existe déjà pour cet utilisateur.");
        }
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        return repository.save(profile);
    }

    public Optional<FinancialProfile> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    public FinancialProfile update(UUID userId, FinancialProfile patch) {
        FinancialProfile existing = repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil financier introuvable."));

        if (patch.getMonthlyIncome() != null) existing.setMonthlyIncome(patch.getMonthlyIncome());
        if (patch.getEmploymentStatus() != null) existing.setEmploymentStatus(patch.getEmploymentStatus());
        if (patch.getAge() > 0) existing.setAge(patch.getAge());
        if (patch.getFinancialExperienceLevel() != null) existing.setFinancialExperienceLevel(patch.getFinancialExperienceLevel());
        if (patch.getCurrency() != null) existing.setCurrency(patch.getCurrency());
        existing.setUpdatedAt(LocalDateTime.now());

        return repository.save(existing);
    }
}
