package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.domain.dto.TreatyDto;
import com.betochimas.historical_conflicts_api.domain.mapper.TreatyMapper;
import com.betochimas.historical_conflicts_api.repository.TreatyRepository;
import com.betochimas.historical_conflicts_api.service.TreatyService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TreatyServiceImpl implements TreatyService {

    private final TreatyRepository treatyRepository;
    private final TreatyMapper mapper;

    public TreatyServiceImpl(TreatyRepository treatyRepository, TreatyMapper mapper) {
        this.treatyRepository = treatyRepository;
        this.mapper = mapper;
    }

    @Override
    public TreatyDto create(TreatyDto dto) {
        return mapper.toDto(treatyRepository.save(mapper.toEntity(dto)));
    }

    @Override
    public Page<TreatyDto> findAll(Pageable pageable) {
        return treatyRepository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<TreatyDto> findByConflictId(Long conflictId, Pageable pageable) {
        return treatyRepository.findByConflictId(conflictId, pageable).map(mapper::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.TREATIES, key = "#id", unless = "#result == null")
    public Optional<TreatyDto> findOne(Long id) {
        return treatyRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return treatyRepository.existsById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.TREATIES, key = "#id")
    public TreatyDto fullUpdate(Long id, TreatyDto dto) {
        dto.setId(id);
        return mapper.toDto(treatyRepository.save(mapper.toEntity(dto)));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.TREATIES, key = "#id")
    public Optional<TreatyDto> partialUpdate(Long id, TreatyDto dto) {
        return treatyRepository.findById(id).map(existing -> {
            mapper.update(existing, dto);
            return mapper.toDto(treatyRepository.save(existing));
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.TREATIES, key = "#id")
    public void delete(Long id) {
        treatyRepository.deleteById(id);
    }
}
