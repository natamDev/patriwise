package com.finmate.domain.port;

import com.finmate.domain.model.UserBadge;

import java.util.List;
import java.util.UUID;

public interface UserBadgeRepository {

    UserBadge save(UserBadge userBadge);

    List<UserBadge> findByUserId(UUID userId);

    boolean existsByUserIdAndBadgeId(UUID userId, String badgeId);
}
