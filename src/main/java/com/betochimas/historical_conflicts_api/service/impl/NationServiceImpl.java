package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.domain.mapper.NationMapper;
import com.betochimas.historical_conflicts_api.repository.NationRepository;
import com.betochimas.historical_conflicts_api.service.NationService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NationServiceImpl implements NationService {

    private final NationRepository nationRepository;
    private final NationMapper mapper;

    public NationServiceImpl(NationRepository nationRepository, NationMapper mapper) {
        this.nationRepository = nationRepository;
        this.mapper = mapper;
    }

    @Override
    public NationDto create(NationDto nationDto) {
        return mapper.toDto(nationRepository.save(mapper.toEntity(nationDto)));
    }

    @Override
    public Page<NationDto> findAll(Pageable pageable) {
        return nationRepository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    // #result is the unwrapped value (Spring unwraps Optional), so guard against caching a miss.
    @Cacheable(cacheNames = CacheConfig.NATIONS, key = "#id", unless = "#result == null")
    public Optional<NationDto> findOne(Long id) {
        return nationRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return nationRepository.existsById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.NATIONS, key = "#id")
    public NationDto fullUpdate(Long id, NationDto nationDto) {
        nationDto.setId(id);
        return mapper.toDto(nationRepository.save(mapper.toEntity(nationDto)));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.NATIONS, key = "#id")
    public Optional<NationDto> partialUpdate(Long id, NationDto nationDto) {
        return nationRepository.findById(id).map(existing -> {
            mapper.update(existing, nationDto);
            return mapper.toDto(nationRepository.save(existing));
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.NATIONS, key = "#id")
    public void delete(Long id) {
        nationRepository.deleteById(id);
    }
}
