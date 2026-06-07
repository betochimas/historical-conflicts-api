package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.TreatyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TreatyService extends CrudService<TreatyDto> {
    Page<TreatyDto> findByConflictId(Long conflictId, Pageable pageable);
}
