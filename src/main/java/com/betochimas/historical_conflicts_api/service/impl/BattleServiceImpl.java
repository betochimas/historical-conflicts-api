package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.EntityNotFoundException;
import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.domain.model.BattleEntity;
import com.betochimas.historical_conflicts_api.domain.model.ConflictEntity;
import com.betochimas.historical_conflicts_api.repository.BattleRepository;
import com.betochimas.historical_conflicts_api.repository.ConflictRepository;
import com.betochimas.historical_conflicts_api.service.BattleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BattleServiceImpl implements BattleService {

    private final BattleRepository battleRepository;
    private final ConflictRepository conflictRepository;

    public BattleServiceImpl(BattleRepository battleRepository, ConflictRepository conflictRepository) {
        this.battleRepository = battleRepository;
        this.conflictRepository = conflictRepository;
    }

    @Override
    public BattleDto create(BattleDto battleDto) {
        return toDto(battleRepository.save(toEntity(battleDto)));
    }

    @Override
    public Page<BattleDto> findAll(Pageable pageable) {
        return battleRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<BattleDto> findByConflictId(Long conflictId, Pageable pageable) {
        return battleRepository.findByConflictId(conflictId, pageable).map(this::toDto);
    }

    @Override
    public Optional<BattleDto> findOne(Long id) {
        return battleRepository.findById(id).map(this::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return battleRepository.existsById(id);
    }

    @Override
    public BattleDto fullUpdate(Long id, BattleDto battleDto) {
        battleDto.setId(id);
        return toDto(battleRepository.save(toEntity(battleDto)));
    }

    @Override
    public Optional<BattleDto> partialUpdate(Long id, BattleDto battleDto) {
        return battleRepository.findById(id).map(existing -> {
            Optional.ofNullable(battleDto.getConflictId())
                    .flatMap(conflictRepository::findById)
                    .ifPresent(existing::setConflict);
            Optional.ofNullable(battleDto.getName()).ifPresent(existing::setName);
            Optional.ofNullable(battleDto.getDate()).ifPresent(existing::setDate);
            Optional.ofNullable(battleDto.getLocation()).ifPresent(existing::setLocation);
            Optional.ofNullable(battleDto.getTerrain()).ifPresent(existing::setTerrain);
            Optional.ofNullable(battleDto.getOutcome()).ifPresent(existing::setOutcome);
            Optional.ofNullable(battleDto.getDescription()).ifPresent(existing::setDescription);
            return toDto(battleRepository.save(existing));
        });
    }

    @Override
    public void delete(Long id) {
        battleRepository.deleteById(id);
    }

    private BattleEntity toEntity(BattleDto dto) {
        ConflictEntity conflict = conflictRepository.findById(dto.getConflictId())
                .orElseThrow(() -> new EntityNotFoundException("Conflict", dto.getConflictId()));
        BattleEntity entity = new BattleEntity();
        entity.setId(dto.getId());
        entity.setConflict(conflict);
        entity.setName(dto.getName());
        entity.setDate(dto.getDate());
        entity.setLocation(dto.getLocation());
        entity.setTerrain(dto.getTerrain());
        entity.setOutcome(dto.getOutcome());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private BattleDto toDto(BattleEntity entity) {
        return BattleDto.builder()
                .id(entity.getId())
                .conflictId(entity.getConflict().getId())
                .name(entity.getName())
                .date(entity.getDate())
                .location(entity.getLocation())
                .terrain(entity.getTerrain())
                .outcome(entity.getOutcome())
                .description(entity.getDescription())
                .build();
    }
}
