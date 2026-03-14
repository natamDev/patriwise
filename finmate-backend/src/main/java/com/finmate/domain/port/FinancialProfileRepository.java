package com.finmate.domain.port;

import com.finmate.domain.model.FinancialProfile;

import java.util.Optional;
import java.util.UUID;

public interface FinancialProfileRepository {

    FinancialProfile save(FinancialProfile profile);

    Optional<FinancialProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
