package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.config.EntityNotFoundException;
import com.betochimas.historical_conflicts_api.config.InvalidRelationshipException;
import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.domain.model.BattleEntity;
import com.betochimas.historical_conflicts_api.domain.model.ConflictEntity;
import com.betochimas.historical_conflicts_api.domain.model.TheaterEntity;
import com.betochimas.historical_conflicts_api.repository.BattleRepository;
import com.betochimas.historical_conflicts_api.repository.ConflictRepository;
import com.betochimas.historical_conflicts_api.repository.TheaterRepository;
import com.betochimas.historical_conflicts_api.service.BattleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BattleServiceImpl implements BattleService {

    private final BattleRepository battleRepository;
    private final ConflictRepository conflictRepository;
    private final TheaterRepository theaterRepository;

    public BattleServiceImpl(BattleRepository battleRepository,
                             ConflictRepository conflictRepository,
                             TheaterRepository theaterRepository) {
        this.battleRepository = battleRepository;
        this.conflictRepository = conflictRepository;
        this.theaterRepository = theaterRepository;
    }

    @Override
    // Attaching a battle to a theater changes that theater's battleIds → evict the theater cache.
    @CacheEvict(cacheNames = CacheConfig.THEATERS, allEntries = true)
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
    public Page<BattleDto> findByTheaterId(Long theaterId, Pageable pageable) {
        return battleRepository.findByTheaterId(theaterId, pageable).map(this::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.BATTLES, key = "#id", unless = "#result == null")
    public Optional<BattleDto> findOne(Long id) {
        return battleRepository.findById(id).map(this::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return battleRepository.existsById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.BATTLES, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.THEATERS, allEntries = true)
    })
    public BattleDto fullUpdate(Long id, BattleDto battleDto) {
        battleDto.setId(id);
        return toDto(battleRepository.save(toEntity(battleDto)));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.BATTLES, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.THEATERS, allEntries = true)
    })
    public Optional<BattleDto> partialUpdate(Long id, BattleDto battleDto) {
        return battleRepository.findById(id).map(existing -> {
            Optional.ofNullable(battleDto.getConflictId())
                    .flatMap(conflictRepository::findById)
                    .ifPresent(existing::setConflict);
            Optional.ofNullable(battleDto.getTheaterId())
                    .flatMap(theaterRepository::findById)
                    .ifPresent(existing::setTheater);
            Optional.ofNullable(battleDto.getName()).ifPresent(existing::setName);
            Optional.ofNullable(battleDto.getDate()).ifPresent(existing::setDate);
            Optional.ofNullable(battleDto.getLocation()).ifPresent(existing::setLocation);
            Optional.ofNullable(battleDto.getTerrain()).ifPresent(existing::setTerrain);
            Optional.ofNullable(battleDto.getOutcome()).ifPresent(existing::setOutcome);
            Optional.ofNullable(battleDto.getDescription()).ifPresent(existing::setDescription);
            // Re-check the (possibly newly-changed) theater against the (possibly newly-changed) conflict.
            if (existing.getTheater() != null) {
                requireSameConflict(existing.getTheater(), existing.getConflict());
            }
            return toDto(battleRepository.save(existing));
        });
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.BATTLES, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.THEATERS, allEntries = true)
    })
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
        if (dto.getTheaterId() != null) {
            TheaterEntity theater = theaterRepository.findById(dto.getTheaterId())
                    .orElseThrow(() -> new EntityNotFoundException("Theater", dto.getTheaterId()));
            requireSameConflict(theater, conflict);
            entity.setTheater(theater);
        }
        return entity;
    }

    private void requireSameConflict(TheaterEntity theater, ConflictEntity conflict) {
        if (!theater.getConflict().getId().equals(conflict.getId())) {
            throw new InvalidRelationshipException(
                    "Battle's conflict " + conflict.getId() + " must match theater " + theater.getId()
                            + "'s conflict " + theater.getConflict().getId());
        }
    }

    private BattleDto toDto(BattleEntity entity) {
        return BattleDto.builder()
                .id(entity.getId())
                .conflictId(entity.getConflict().getId())
                .theaterId(entity.getTheater() != null ? entity.getTheater().getId() : null)
                .name(entity.getName())
                .date(entity.getDate())
                .location(entity.getLocation())
                .terrain(entity.getTerrain())
                .outcome(entity.getOutcome())
                .description(entity.getDescription())
                .build();
    }
}
