package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.config.InvalidRelationshipException;
import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.domain.mapper.BattleMapper;
import com.betochimas.historical_conflicts_api.domain.model.BattleEntity;
import com.betochimas.historical_conflicts_api.repository.BattleRepository;
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
    private final BattleMapper mapper;

    public BattleServiceImpl(BattleRepository battleRepository, BattleMapper mapper) {
        this.battleRepository = battleRepository;
        this.mapper = mapper;
    }

    @Override
    // Attaching a battle to a theater changes that theater's battleIds → evict the theater cache.
    @CacheEvict(cacheNames = CacheConfig.THEATERS, allEntries = true)
    public BattleDto create(BattleDto battleDto) {
        BattleEntity entity = mapper.toEntity(battleDto);
        validateTheaterConflict(entity);
        return mapper.toDto(battleRepository.save(entity));
    }

    @Override
    public Page<BattleDto> findAll(Pageable pageable) {
        return battleRepository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<BattleDto> findByConflictId(Long conflictId, Pageable pageable) {
        return battleRepository.findByConflictId(conflictId, pageable).map(mapper::toDto);
    }

    @Override
    public Page<BattleDto> findByTheaterId(Long theaterId, Pageable pageable) {
        return battleRepository.findByTheaterId(theaterId, pageable).map(mapper::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.BATTLES, key = "#id", unless = "#result == null")
    public Optional<BattleDto> findOne(Long id) {
        return battleRepository.findById(id).map(mapper::toDto);
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
        BattleEntity entity = mapper.toEntity(battleDto);
        validateTheaterConflict(entity);
        return mapper.toDto(battleRepository.save(entity));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.BATTLES, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.THEATERS, allEntries = true)
    })
    public Optional<BattleDto> partialUpdate(Long id, BattleDto battleDto) {
        return battleRepository.findById(id).map(existing -> {
            mapper.update(existing, battleDto);
            // Re-check the (possibly newly-changed) theater against the (possibly newly-changed) conflict.
            validateTheaterConflict(existing);
            return mapper.toDto(battleRepository.save(existing));
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

    /** A battle may only sit in a theater of its own conflict; mismatch → 400. */
    private void validateTheaterConflict(BattleEntity battle) {
        if (battle.getTheater() != null
                && !battle.getTheater().getConflict().getId().equals(battle.getConflict().getId())) {
            throw new InvalidRelationshipException(
                    "Battle's conflict " + battle.getConflict().getId() + " must match theater "
                            + battle.getTheater().getId() + "'s conflict "
                            + battle.getTheater().getConflict().getId());
        }
    }
}
