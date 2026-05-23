package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.domain.model.NationEntity;
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

    public NationServiceImpl(NationRepository nationRepository) {
        this.nationRepository = nationRepository;
    }

    @Override
    public NationDto create(NationDto nationDto) {
        NationEntity saved = nationRepository.save(toEntity(nationDto));
        return toDto(saved);
    }

    @Override
    public Page<NationDto> findAll(Pageable pageable) {
        return nationRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    // #result is the unwrapped value (Spring unwraps Optional), so guard against caching a miss.
    @Cacheable(cacheNames = CacheConfig.NATIONS, key = "#id", unless = "#result == null")
    public Optional<NationDto> findOne(Long id) {
        return nationRepository.findById(id).map(this::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return nationRepository.existsById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.NATIONS, key = "#id")
    public NationDto fullUpdate(Long id, NationDto nationDto) {
        nationDto.setId(id);
        NationEntity saved = nationRepository.save(toEntity(nationDto));
        return toDto(saved);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.NATIONS, key = "#id")
    public Optional<NationDto> partialUpdate(Long id, NationDto nationDto) {
        return nationRepository.findById(id).map(existing -> {
            Optional.ofNullable(nationDto.getName()).ifPresent(existing::setName);
            Optional.ofNullable(nationDto.getRegion()).ifPresent(existing::setRegion);
            Optional.ofNullable(nationDto.getFoundedYear()).ifPresent(existing::setFoundedYear);
            Optional.ofNullable(nationDto.getDissolvedYear()).ifPresent(existing::setDissolvedYear);
            Optional.ofNullable(nationDto.getDescription()).ifPresent(existing::setDescription);
            return toDto(nationRepository.save(existing));
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.NATIONS, key = "#id")
    public void delete(Long id) {
        nationRepository.deleteById(id);
    }

    private NationEntity toEntity(NationDto dto) {
        NationEntity entity = new NationEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setRegion(dto.getRegion());
        entity.setFoundedYear(dto.getFoundedYear());
        entity.setDissolvedYear(dto.getDissolvedYear());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private NationDto toDto(NationEntity entity) {
        return NationDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .region(entity.getRegion())
                .foundedYear(entity.getFoundedYear())
                .dissolvedYear(entity.getDissolvedYear())
                .description(entity.getDescription())
                .build();
    }
}
