package com.finmate.domain.port;

import com.finmate.domain.model.SavingStreak;

import java.util.Optional;
import java.util.UUID;

public interface SavingStreakRepository {

    Optional<SavingStreak> findByUserId(UUID userId);

    SavingStreak save(SavingStreak streak);
}
