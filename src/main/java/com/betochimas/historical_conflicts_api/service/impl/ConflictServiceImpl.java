package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.domain.mapper.ConflictMapper;
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
    private final ConflictMapper mapper;

    public ConflictServiceImpl(ConflictRepository conflictRepository, ConflictMapper mapper) {
        this.conflictRepository = conflictRepository;
        this.mapper = mapper;
    }

    @Override
    public ConflictDto create(ConflictDto conflictDto) {
        return mapper.toDto(conflictRepository.save(mapper.toEntity(conflictDto)));
    }

    @Override
    public Page<ConflictDto> findAll(Pageable pageable) {
        return conflictRepository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.CONFLICTS, key = "#id", unless = "#result == null")
    public Optional<ConflictDto> findOne(Long id) {
        return conflictRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return conflictRepository.existsById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CONFLICTS, key = "#id")
    public ConflictDto fullUpdate(Long id, ConflictDto conflictDto) {
        conflictDto.setId(id);
        return mapper.toDto(conflictRepository.save(mapper.toEntity(conflictDto)));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CONFLICTS, key = "#id")
    public Optional<ConflictDto> partialUpdate(Long id, ConflictDto conflictDto) {
        return conflictRepository.findById(id).map(existing -> {
            mapper.update(existing, conflictDto);
            return mapper.toDto(conflictRepository.save(existing));
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CONFLICTS, key = "#id")
    public void delete(Long id) {
        conflictRepository.deleteById(id);
    }
}
