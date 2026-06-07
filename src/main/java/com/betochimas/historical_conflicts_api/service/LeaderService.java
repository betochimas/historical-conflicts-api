package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.LeaderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaderService extends CrudService<LeaderDto> {
    Page<LeaderDto> findByNationId(Long nationId, Pageable pageable);
}
