package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.TheaterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TheaterService extends CrudService<TheaterDto> {
    Page<TheaterDto> findByConflictId(Long conflictId, Pageable pageable);
}
