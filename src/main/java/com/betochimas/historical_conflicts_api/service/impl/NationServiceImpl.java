package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.domain.model.NationEntity;
import com.betochimas.historical_conflicts_api.repository.NationRepository;
import com.betochimas.historical_conflicts_api.service.NationService;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<NationDto> findAll() {
        return nationRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public Optional<NationDto> findOne(Long id) {
        return nationRepository.findById(id).map(this::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return nationRepository.existsById(id);
    }

    @Override
    public NationDto fullUpdate(Long id, NationDto nationDto) {
        nationDto.setId(id);
        NationEntity saved = nationRepository.save(toEntity(nationDto));
        return toDto(saved);
    }

    @Override
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
