package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.config.EntityNotFoundException;
import com.betochimas.historical_conflicts_api.config.InvalidRelationshipException;
import com.betochimas.historical_conflicts_api.domain.dto.TheaterDto;
import com.betochimas.historical_conflicts_api.domain.model.BattleEntity;
import com.betochimas.historical_conflicts_api.domain.model.ConflictEntity;
import com.betochimas.historical_conflicts_api.domain.model.TheaterEntity;
import com.betochimas.historical_conflicts_api.repository.BattleRepository;
import com.betochimas.historical_conflicts_api.repository.ConflictRepository;
import com.betochimas.historical_conflicts_api.repository.TheaterRepository;
import com.betochimas.historical_conflicts_api.service.TheaterService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;
    private final ConflictRepository conflictRepository;
    private final BattleRepository battleRepository;

    public TheaterServiceImpl(TheaterRepository theaterRepository,
                              ConflictRepository conflictRepository,
                              BattleRepository battleRepository) {
        this.theaterRepository = theaterRepository;
        this.conflictRepository = conflictRepository;
        this.battleRepository = battleRepository;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.BATTLES, allEntries = true)
    public TheaterDto create(TheaterDto dto) {
        TheaterEntity saved = theaterRepository.save(toEntity(dto));
        applyBattleIds(saved, dto.getBattleIds());
        return toDto(saved);
    }

    @Override
    public Page<TheaterDto> findAll(Pageable pageable) {
        return theaterRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<TheaterDto> findByConflictId(Long conflictId, Pageable pageable) {
        return theaterRepository.findByConflictId(conflictId, pageable).map(this::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.THEATERS, key = "#id", unless = "#result == null")
    public Optional<TheaterDto> findOne(Long id) {
        return theaterRepository.findById(id).map(this::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return theaterRepository.existsById(id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.THEATERS, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.BATTLES, allEntries = true)
    })
    public TheaterDto fullUpdate(Long id, TheaterDto dto) {
        dto.setId(id);
        TheaterEntity saved = theaterRepository.save(toEntity(dto));
        applyBattleIds(saved, dto.getBattleIds());
        return toDto(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.THEATERS, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.BATTLES, allEntries = true)
    })
    public Optional<TheaterDto> partialUpdate(Long id, TheaterDto dto) {
        return theaterRepository.findById(id).map(existing -> {
            Optional.ofNullable(dto.getConflictId())
                    .flatMap(conflictRepository::findById)
                    .ifPresent(existing::setConflict);
            Optional.ofNullable(dto.getName()).ifPresent(existing::setName);
            Optional.ofNullable(dto.getRegion()).ifPresent(existing::setRegion);
            Optional.ofNullable(dto.getStartDate()).ifPresent(existing::setStartDate);
            Optional.ofNullable(dto.getEndDate()).ifPresent(existing::setEndDate);
            Optional.ofNullable(dto.getOutcome()).ifPresent(existing::setOutcome);
            Optional.ofNullable(dto.getDescription()).ifPresent(existing::setDescription);
            TheaterEntity saved = theaterRepository.save(existing);
            applyBattleIds(saved, dto.getBattleIds());
            return toDto(saved);
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.THEATERS, key = "#id")
    public void delete(Long id) {
        // A theater that still has battles can't be deleted (FK RESTRICT) → surfaces as 409.
        theaterRepository.deleteById(id);
    }

    /**
     * Additive/reassign membership: each listed battle is attached (or moved) to this theater,
     * after verifying it belongs to the same conflict. Unlisted battles are left untouched.
     * A null list means "no change to membership".
     */
    private void applyBattleIds(TheaterEntity theater, List<Long> battleIds) {
        if (battleIds == null) {
            return;
        }
        for (Long battleId : battleIds) {
            BattleEntity battle = battleRepository.findById(battleId)
                    .orElseThrow(() -> new EntityNotFoundException("Battle", battleId));
            if (!battle.getConflict().getId().equals(theater.getConflict().getId())) {
                throw new InvalidRelationshipException(
                        "Battle " + battleId + " belongs to conflict " + battle.getConflict().getId()
                                + ", but theater " + theater.getId() + " belongs to conflict "
                                + theater.getConflict().getId());
            }
            battle.setTheater(theater);
            battleRepository.save(battle);
        }
    }

    private TheaterEntity toEntity(TheaterDto dto) {
        ConflictEntity conflict = conflictRepository.findById(dto.getConflictId())
                .orElseThrow(() -> new EntityNotFoundException("Conflict", dto.getConflictId()));
        TheaterEntity entity = new TheaterEntity();
        entity.setId(dto.getId());
        entity.setConflict(conflict);
        entity.setName(dto.getName());
        entity.setRegion(dto.getRegion());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setOutcome(dto.getOutcome());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private TheaterDto toDto(TheaterEntity entity) {
        return TheaterDto.builder()
                .id(entity.getId())
                .conflictId(entity.getConflict().getId())
                .name(entity.getName())
                .region(entity.getRegion())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .outcome(entity.getOutcome())
                .description(entity.getDescription())
                .battleIds(battleRepository.findIdsByTheaterId(entity.getId()))
                .build();
    }
}
