package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;

import java.util.List;
import java.util.Optional;

public interface ConflictParticipantService {
    ConflictParticipantDto create(ConflictParticipantDto dto);
    List<ConflictParticipantDto> findAll();
    List<ConflictParticipantDto> findByConflictId(Long conflictId);
    Optional<ConflictParticipantDto> findOne(Long id);
    boolean isExists(Long id);
    ConflictParticipantDto fullUpdate(Long id, ConflictParticipantDto dto);
    Optional<ConflictParticipantDto> partialUpdate(Long id, ConflictParticipantDto dto);
    void delete(Long id);
}
