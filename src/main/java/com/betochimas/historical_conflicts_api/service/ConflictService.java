package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;

import java.util.List;
import java.util.Optional;

public interface ConflictService {
    ConflictDto create(ConflictDto conflictDto);
    List<ConflictDto> findAll();
    Optional<ConflictDto> findOne(Long id);
    boolean isExists(Long id);
    ConflictDto fullUpdate(Long id, ConflictDto conflictDto);
    Optional<ConflictDto> partialUpdate(Long id, ConflictDto conflictDto);
    void delete(Long id);
}
