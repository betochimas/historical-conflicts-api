package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.LeaderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LeaderService {
    LeaderDto create(LeaderDto leaderDto);
    Page<LeaderDto> findAll(Pageable pageable);
    Page<LeaderDto> findByNationId(Long nationId, Pageable pageable);
    Optional<LeaderDto> findOne(Long id);
    boolean isExists(Long id);
    LeaderDto fullUpdate(Long id, LeaderDto leaderDto);
    Optional<LeaderDto> partialUpdate(Long id, LeaderDto leaderDto);
    void delete(Long id);
}
