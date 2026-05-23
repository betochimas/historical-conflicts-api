package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NationService {
    NationDto create(NationDto nationDto);
    Page<NationDto> findAll(Pageable pageable);
    Optional<NationDto> findOne(Long id);
    boolean isExists(Long id);
    NationDto fullUpdate(Long id, NationDto nationDto);
    Optional<NationDto> partialUpdate(Long id, NationDto nationDto);
    void delete(Long id);
}
