package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.NationDto;

import java.util.List;
import java.util.Optional;

public interface NationService {
    NationDto create(NationDto nationDto);
    List<NationDto> findAll();
    Optional<NationDto> findOne(Long id);
    boolean isExists(Long id);
    NationDto fullUpdate(Long id, NationDto nationDto);
    Optional<NationDto> partialUpdate(Long id, NationDto nationDto);
    void delete(Long id);
}
