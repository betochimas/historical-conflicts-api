package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictAtlasDto;

public interface ConflictAtlasService {

    /**
     * Builds the aggregate atlas for one conflict (its theaters, date-sorted battles,
     * denormalized participants, and computed stats).
     *
     * @throws com.betochimas.historical_conflicts_api.config.EntityNotFoundException if no
     *         conflict has the given id (surfaced as 404)
     */
    ConflictAtlasDto getAtlas(Long conflictId);
}
