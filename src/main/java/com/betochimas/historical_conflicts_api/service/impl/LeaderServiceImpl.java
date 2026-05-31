package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.config.EntityNotFoundException;
import com.betochimas.historical_conflicts_api.domain.dto.LeaderDto;
import com.betochimas.historical_conflicts_api.domain.model.LeaderEntity;
import com.betochimas.historical_conflicts_api.domain.model.NationEntity;
import com.betochimas.historical_conflicts_api.repository.LeaderRepository;
import com.betochimas.historical_conflicts_api.repository.NationRepository;
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
    private final NationRepository nationRepository;

    public LeaderServiceImpl(LeaderRepository leaderRepository,
                             NationRepository nationRepository) {
        this.leaderRepository = leaderRepository;
        this.nationRepository = nationRepository;
    }

    @Override
    public LeaderDto create(LeaderDto leaderDto) {
        return toDto(leaderRepository.save(toEntity(leaderDto)));
    }

    @Override
    public Page<LeaderDto> findAll(Pageable pageable) {
        return leaderRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<LeaderDto> findByNationId(Long nationId, Pageable pageable) {
        return leaderRepository.findByNationId(nationId, pageable).map(this::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.LEADERS, key = "#id", unless = "#result == null")
    public Optional<LeaderDto> findOne(Long id) {
        return leaderRepository.findById(id).map(this::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return leaderRepository.existsById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.LEADERS, key = "#id")
    public LeaderDto fullUpdate(Long id, LeaderDto leaderDto) {
        leaderDto.setId(id);
        return toDto(leaderRepository.save(toEntity(leaderDto)));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.LEADERS, key = "#id")
    public Optional<LeaderDto> partialUpdate(Long id, LeaderDto leaderDto) {
        return leaderRepository.findById(id).map(existing -> {
            Optional.ofNullable(leaderDto.getNationId())
                    .flatMap(nationRepository::findById)
                    .ifPresent(existing::setNation);
            Optional.ofNullable(leaderDto.getName()).ifPresent(existing::setName);
            Optional.ofNullable(leaderDto.getRole()).ifPresent(existing::setRole);
            Optional.ofNullable(leaderDto.getTitle()).ifPresent(existing::setTitle);
            Optional.ofNullable(leaderDto.getBirthYear()).ifPresent(existing::setBirthYear);
            Optional.ofNullable(leaderDto.getDeathYear()).ifPresent(existing::setDeathYear);
            Optional.ofNullable(leaderDto.getDescription()).ifPresent(existing::setDescription);
            return toDto(leaderRepository.save(existing));
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.LEADERS, key = "#id")
    public void delete(Long id) {
        leaderRepository.deleteById(id);
    }

    private LeaderEntity toEntity(LeaderDto dto) {
        NationEntity nation = nationRepository.findById(dto.getNationId())
                .orElseThrow(() -> new EntityNotFoundException("Nation", dto.getNationId()));
        LeaderEntity entity = new LeaderEntity();
        entity.setId(dto.getId());
        entity.setNation(nation);
        entity.setName(dto.getName());
        entity.setRole(dto.getRole());
        entity.setTitle(dto.getTitle());
        entity.setBirthYear(dto.getBirthYear());
        entity.setDeathYear(dto.getDeathYear());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private LeaderDto toDto(LeaderEntity entity) {
        return LeaderDto.builder()
                .id(entity.getId())
                .nationId(entity.getNation().getId())
                .name(entity.getName())
                .role(entity.getRole())
                .title(entity.getTitle())
                .birthYear(entity.getBirthYear())
                .deathYear(entity.getDeathYear())
                .description(entity.getDescription())
                .build();
    }
}
