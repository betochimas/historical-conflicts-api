package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.EntityNotFoundException;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import com.betochimas.historical_conflicts_api.domain.model.ConflictEntity;
import com.betochimas.historical_conflicts_api.domain.model.ConflictParticipantEntity;
import com.betochimas.historical_conflicts_api.domain.model.NationEntity;
import com.betochimas.historical_conflicts_api.repository.ConflictParticipantRepository;
import com.betochimas.historical_conflicts_api.repository.ConflictRepository;
import com.betochimas.historical_conflicts_api.repository.NationRepository;
import com.betochimas.historical_conflicts_api.service.ConflictParticipantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConflictParticipantServiceImpl implements ConflictParticipantService {

    private final ConflictParticipantRepository participantRepository;
    private final ConflictRepository conflictRepository;
    private final NationRepository nationRepository;

    public ConflictParticipantServiceImpl(
            ConflictParticipantRepository participantRepository,
            ConflictRepository conflictRepository,
            NationRepository nationRepository) {
        this.participantRepository = participantRepository;
        this.conflictRepository = conflictRepository;
        this.nationRepository = nationRepository;
    }

    @Override
    public ConflictParticipantDto create(ConflictParticipantDto dto) {
        return toDto(participantRepository.save(toEntity(dto)));
    }

    @Override
    public Page<ConflictParticipantDto> findAll(Pageable pageable) {
        return participantRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<ConflictParticipantDto> findByConflictId(Long conflictId, Pageable pageable) {
        return participantRepository.findByConflictId(conflictId, pageable).map(this::toDto);
    }

    @Override
    public Optional<ConflictParticipantDto> findOne(Long id) {
        return participantRepository.findById(id).map(this::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return participantRepository.existsById(id);
    }

    @Override
    public ConflictParticipantDto fullUpdate(Long id, ConflictParticipantDto dto) {
        dto.setId(id);
        return toDto(participantRepository.save(toEntity(dto)));
    }

    @Override
    public Optional<ConflictParticipantDto> partialUpdate(Long id, ConflictParticipantDto dto) {
        return participantRepository.findById(id).map(existing -> {
            Optional.ofNullable(dto.getConflictId())
                    .flatMap(conflictRepository::findById)
                    .ifPresent(existing::setConflict);
            Optional.ofNullable(dto.getNationId())
                    .flatMap(nationRepository::findById)
                    .ifPresent(existing::setNation);
            Optional.ofNullable(dto.getRole()).ifPresent(existing::setRole);
            Optional.ofNullable(dto.getTroopsCommitted()).ifPresent(existing::setTroopsCommitted);
            Optional.ofNullable(dto.getCasualties()).ifPresent(existing::setCasualties);
            Optional.ofNullable(dto.getOutcome()).ifPresent(existing::setOutcome);
            return toDto(participantRepository.save(existing));
        });
    }

    @Override
    public void delete(Long id) {
        participantRepository.deleteById(id);
    }

    private ConflictParticipantEntity toEntity(ConflictParticipantDto dto) {
        ConflictEntity conflict = conflictRepository.findById(dto.getConflictId())
                .orElseThrow(() -> new EntityNotFoundException("Conflict", dto.getConflictId()));
        NationEntity nation = nationRepository.findById(dto.getNationId())
                .orElseThrow(() -> new EntityNotFoundException("Nation", dto.getNationId()));
        ConflictParticipantEntity entity = new ConflictParticipantEntity();
        entity.setId(dto.getId());
        entity.setConflict(conflict);
        entity.setNation(nation);
        entity.setRole(dto.getRole());
        entity.setTroopsCommitted(dto.getTroopsCommitted());
        entity.setCasualties(dto.getCasualties());
        entity.setOutcome(dto.getOutcome());
        return entity;
    }

    private ConflictParticipantDto toDto(ConflictParticipantEntity entity) {
        return ConflictParticipantDto.builder()
                .id(entity.getId())
                .conflictId(entity.getConflict().getId())
                .nationId(entity.getNation().getId())
                .role(entity.getRole())
                .troopsCommitted(entity.getTroopsCommitted())
                .casualties(entity.getCasualties())
                .outcome(entity.getOutcome())
                .build();
    }
}
