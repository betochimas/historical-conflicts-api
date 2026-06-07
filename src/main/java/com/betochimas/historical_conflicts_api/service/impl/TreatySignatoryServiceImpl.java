package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.domain.dto.TreatySignatoryDto;
import com.betochimas.historical_conflicts_api.domain.mapper.TreatySignatoryMapper;
import com.betochimas.historical_conflicts_api.repository.TreatySignatoryRepository;
import com.betochimas.historical_conflicts_api.service.TreatySignatoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TreatySignatoryServiceImpl implements TreatySignatoryService {

    private final TreatySignatoryRepository signatoryRepository;
    private final TreatySignatoryMapper mapper;

    public TreatySignatoryServiceImpl(TreatySignatoryRepository signatoryRepository,
                                      TreatySignatoryMapper mapper) {
        this.signatoryRepository = signatoryRepository;
        this.mapper = mapper;
    }

    @Override
    public TreatySignatoryDto create(TreatySignatoryDto dto) {
        return mapper.toDto(signatoryRepository.save(mapper.toEntity(dto)));
    }

    @Override
    public Page<TreatySignatoryDto> findAll(Pageable pageable) {
        return signatoryRepository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<TreatySignatoryDto> findByTreatyId(Long treatyId, Pageable pageable) {
        return signatoryRepository.findByTreatyId(treatyId, pageable).map(mapper::toDto);
    }

    @Override
    public Page<TreatySignatoryDto> findByNationId(Long nationId, Pageable pageable) {
        return signatoryRepository.findByNationId(nationId, pageable).map(mapper::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.TREATY_SIGNATORIES, key = "#id", unless = "#result == null")
    public Optional<TreatySignatoryDto> findOne(Long id) {
        return signatoryRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return signatoryRepository.existsById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.TREATY_SIGNATORIES, key = "#id")
    public TreatySignatoryDto fullUpdate(Long id, TreatySignatoryDto dto) {
        dto.setId(id);
        return mapper.toDto(signatoryRepository.save(mapper.toEntity(dto)));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.TREATY_SIGNATORIES, key = "#id")
    public Optional<TreatySignatoryDto> partialUpdate(Long id, TreatySignatoryDto dto) {
        return signatoryRepository.findById(id).map(existing -> {
            mapper.update(existing, dto);
            return mapper.toDto(signatoryRepository.save(existing));
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.TREATY_SIGNATORIES, key = "#id")
    public void delete(Long id) {
        signatoryRepository.deleteById(id);
    }
}
