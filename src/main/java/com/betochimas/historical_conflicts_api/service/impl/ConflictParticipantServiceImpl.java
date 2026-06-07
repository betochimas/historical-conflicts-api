package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import com.betochimas.historical_conflicts_api.domain.mapper.ConflictParticipantMapper;
import com.betochimas.historical_conflicts_api.repository.ConflictParticipantRepository;
import com.betochimas.historical_conflicts_api.service.ConflictParticipantService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConflictParticipantServiceImpl implements ConflictParticipantService {

    private final ConflictParticipantRepository participantRepository;
    private final ConflictParticipantMapper mapper;

    public ConflictParticipantServiceImpl(ConflictParticipantRepository participantRepository,
                                          ConflictParticipantMapper mapper) {
        this.participantRepository = participantRepository;
        this.mapper = mapper;
    }

    @Override
    public ConflictParticipantDto create(ConflictParticipantDto dto) {
        return mapper.toDto(participantRepository.save(mapper.toEntity(dto)));
    }

    @Override
    public Page<ConflictParticipantDto> findAll(Pageable pageable) {
        return participantRepository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<ConflictParticipantDto> findByConflictId(Long conflictId, Pageable pageable) {
        return participantRepository.findByConflictId(conflictId, pageable).map(mapper::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.CONFLICT_PARTICIPANTS, key = "#id", unless = "#result == null")
    public Optional<ConflictParticipantDto> findOne(Long id) {
        return participantRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return participantRepository.existsById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CONFLICT_PARTICIPANTS, key = "#id")
    public ConflictParticipantDto fullUpdate(Long id, ConflictParticipantDto dto) {
        dto.setId(id);
        return mapper.toDto(participantRepository.save(mapper.toEntity(dto)));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CONFLICT_PARTICIPANTS, key = "#id")
    public Optional<ConflictParticipantDto> partialUpdate(Long id, ConflictParticipantDto dto) {
        return participantRepository.findById(id).map(existing -> {
            mapper.update(existing, dto);
            return mapper.toDto(participantRepository.save(existing));
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.CONFLICT_PARTICIPANTS, key = "#id")
    public void delete(Long id) {
        participantRepository.deleteById(id);
    }
}
