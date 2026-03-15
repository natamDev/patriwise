package com.finmate.domain.port;

import com.finmate.domain.model.BehavioralEvent;

import java.util.List;
import java.util.UUID;

public interface BehavioralEventRepository {
    BehavioralEvent save(BehavioralEvent event);
    List<BehavioralEvent> findByUserId(UUID userId);
}
