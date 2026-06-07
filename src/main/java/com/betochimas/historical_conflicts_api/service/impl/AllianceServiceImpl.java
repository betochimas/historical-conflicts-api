package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.domain.dto.AllianceDto;
import com.betochimas.historical_conflicts_api.domain.mapper.AllianceMapper;
import com.betochimas.historical_conflicts_api.repository.AllianceRepository;
import com.betochimas.historical_conflicts_api.service.AllianceService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AllianceServiceImpl implements AllianceService {

    private final AllianceRepository allianceRepository;
    private final AllianceMapper mapper;

    public AllianceServiceImpl(AllianceRepository allianceRepository, AllianceMapper mapper) {
        this.allianceRepository = allianceRepository;
        this.mapper = mapper;
    }

    @Override
    public AllianceDto create(AllianceDto dto) {
        return mapper.toDto(allianceRepository.save(mapper.toEntity(dto)));
    }

    @Override
    public Page<AllianceDto> findAll(Pageable pageable) {
        return allianceRepository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.ALLIANCES, key = "#id", unless = "#result == null")
    public Optional<AllianceDto> findOne(Long id) {
        return allianceRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return allianceRepository.existsById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.ALLIANCES, key = "#id")
    public AllianceDto fullUpdate(Long id, AllianceDto dto) {
        dto.setId(id);
        return mapper.toDto(allianceRepository.save(mapper.toEntity(dto)));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.ALLIANCES, key = "#id")
    public Optional<AllianceDto> partialUpdate(Long id, AllianceDto dto) {
        return allianceRepository.findById(id).map(existing -> {
            mapper.update(existing, dto);
            return mapper.toDto(allianceRepository.save(existing));
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.ALLIANCES, key = "#id")
    public void delete(Long id) {
        allianceRepository.deleteById(id);
    }
}
