package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.TheaterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TheaterService {
    TheaterDto create(TheaterDto theaterDto);
    Page<TheaterDto> findAll(Pageable pageable);
    Page<TheaterDto> findByConflictId(Long conflictId, Pageable pageable);
    Optional<TheaterDto> findOne(Long id);
    boolean isExists(Long id);
    TheaterDto fullUpdate(Long id, TheaterDto theaterDto);
    Optional<TheaterDto> partialUpdate(Long id, TheaterDto theaterDto);
    void delete(Long id);
}
