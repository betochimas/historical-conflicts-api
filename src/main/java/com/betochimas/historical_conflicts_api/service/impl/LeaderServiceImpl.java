package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.domain.dto.LeaderDto;
import com.betochimas.historical_conflicts_api.domain.mapper.LeaderMapper;
import com.betochimas.historical_conflicts_api.repository.LeaderRepository;
import com.betochimas.historical_conflicts_api.service.LeaderService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LeaderServiceImpl implements LeaderService {

    private final LeaderRepository leaderRepository;
    private final LeaderMapper mapper;

    public LeaderServiceImpl(LeaderRepository leaderRepository, LeaderMapper mapper) {
        this.leaderRepository = leaderRepository;
        this.mapper = mapper;
    }

    @Override
    public LeaderDto create(LeaderDto leaderDto) {
        return mapper.toDto(leaderRepository.save(mapper.toEntity(leaderDto)));
    }

    @Override
    public Page<LeaderDto> findAll(Pageable pageable) {
        return leaderRepository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<LeaderDto> findByNationId(Long nationId, Pageable pageable) {
        return leaderRepository.findByNationId(nationId, pageable).map(mapper::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.LEADERS, key = "#id", unless = "#result == null")
    public Optional<LeaderDto> findOne(Long id) {
        return leaderRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return leaderRepository.existsById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.LEADERS, key = "#id")
    public LeaderDto fullUpdate(Long id, LeaderDto leaderDto) {
        leaderDto.setId(id);
        return mapper.toDto(leaderRepository.save(mapper.toEntity(leaderDto)));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.LEADERS, key = "#id")
    public Optional<LeaderDto> partialUpdate(Long id, LeaderDto leaderDto) {
        return leaderRepository.findById(id).map(existing -> {
            mapper.update(existing, leaderDto);
            return mapper.toDto(leaderRepository.save(existing));
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.LEADERS, key = "#id")
    public void delete(Long id) {
        leaderRepository.deleteById(id);
    }
}
