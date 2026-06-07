package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BattleService extends CrudService<BattleDto> {
    Page<BattleDto> findByConflictId(Long conflictId, Pageable pageable);
    Page<BattleDto> findByTheaterId(Long theaterId, Pageable pageable);
}
