package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ConflictParticipantService {
    ConflictParticipantDto create(ConflictParticipantDto dto);
    Page<ConflictParticipantDto> findAll(Pageable pageable);
    Page<ConflictParticipantDto> findByConflictId(Long conflictId, Pageable pageable);
    Optional<ConflictParticipantDto> findOne(Long id);
    boolean isExists(Long id);
    ConflictParticipantDto fullUpdate(Long id, ConflictParticipantDto dto);
    Optional<ConflictParticipantDto> partialUpdate(Long id, ConflictParticipantDto dto);
    void delete(Long id);
}
