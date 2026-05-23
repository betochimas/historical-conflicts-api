package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.domain.model.ConflictEntity;
import com.betochimas.historical_conflicts_api.repository.ConflictRepository;
import com.betochimas.historical_conflicts_api.service.ConflictService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConflictServiceImpl implements ConflictService {

    private final ConflictRepository conflictRepository;

    public ConflictServiceImpl(ConflictRepository conflictRepository) {
        this.conflictRepository = conflictRepository;
    }

    @Override
    public ConflictDto create(ConflictDto conflictDto) {
        return toDto(conflictRepository.save(toEntity(conflictDto)));
    }

    @Override
    public Page<ConflictDto> findAll(Pageable pageable) {
        return conflictRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.CONFLICTS, key = "#id", unless = "#result == null")
    public Optional<ConflictDto> findOne(Long id) {
        return conflictRepository.findById(id).map(this::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return conflictRepository.existsById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CONFLICTS, key = "#id")
    public ConflictDto fullUpdate(Long id, ConflictDto conflictDto) {
        conflictDto.setId(id);
        return toDto(conflictRepository.save(toEntity(conflictDto)));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CONFLICTS, key = "#id")
    public Optional<ConflictDto> partialUpdate(Long id, ConflictDto conflictDto) {
        return conflictRepository.findById(id).map(existing -> {
            Optional.ofNullable(conflictDto.getName()).ifPresent(existing::setName);
            Optional.ofNullable(conflictDto.getConflictType()).ifPresent(existing::setConflictType);
            Optional.ofNullable(conflictDto.getStartDate()).ifPresent(existing::setStartDate);
            Optional.ofNullable(conflictDto.getEndDate()).ifPresent(existing::setEndDate);
            Optional.ofNullable(conflictDto.getOutcome()).ifPresent(existing::setOutcome);
            Optional.ofNullable(conflictDto.getDescription()).ifPresent(existing::setDescription);
            return toDto(conflictRepository.save(existing));
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CONFLICTS, key = "#id")
    public void delete(Long id) {
        conflictRepository.deleteById(id);
    }

    private ConflictEntity toEntity(ConflictDto dto) {
        ConflictEntity entity = new ConflictEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setConflictType(dto.getConflictType());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setOutcome(dto.getOutcome());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private ConflictDto toDto(ConflictEntity entity) {
        return ConflictDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .conflictType(entity.getConflictType())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .outcome(entity.getOutcome())
                .description(entity.getDescription())
                .build();
    }
}
