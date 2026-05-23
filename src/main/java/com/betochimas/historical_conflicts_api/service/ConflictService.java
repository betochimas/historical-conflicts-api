package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ConflictService {
    ConflictDto create(ConflictDto conflictDto);
    Page<ConflictDto> findAll(Pageable pageable);
    Optional<ConflictDto> findOne(Long id);
    boolean isExists(Long id);
    ConflictDto fullUpdate(Long id, ConflictDto conflictDto);
    Optional<ConflictDto> partialUpdate(Long id, ConflictDto conflictDto);
    void delete(Long id);
}
