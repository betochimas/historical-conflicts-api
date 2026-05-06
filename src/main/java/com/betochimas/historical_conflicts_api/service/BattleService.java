package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;

import java.util.List;
import java.util.Optional;

public interface BattleService {
    BattleDto create(BattleDto battleDto);
    List<BattleDto> findAll();
    List<BattleDto> findByConflictId(Long conflictId);
    Optional<BattleDto> findOne(Long id);
    boolean isExists(Long id);
    BattleDto fullUpdate(Long id, BattleDto battleDto);
    Optional<BattleDto> partialUpdate(Long id, BattleDto battleDto);
    void delete(Long id);
}
