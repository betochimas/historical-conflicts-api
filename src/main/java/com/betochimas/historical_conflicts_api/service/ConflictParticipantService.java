package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConflictParticipantService extends CrudService<ConflictParticipantDto> {
    Page<ConflictParticipantDto> findByConflictId(Long conflictId, Pageable pageable);
}
