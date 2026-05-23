package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BattleService {
    BattleDto create(BattleDto battleDto);
    Page<BattleDto> findAll(Pageable pageable);
    Page<BattleDto> findByConflictId(Long conflictId, Pageable pageable);
    Optional<BattleDto> findOne(Long id);
    boolean isExists(Long id);
    BattleDto fullUpdate(Long id, BattleDto battleDto);
    Optional<BattleDto> partialUpdate(Long id, BattleDto battleDto);
    void delete(Long id);
}
