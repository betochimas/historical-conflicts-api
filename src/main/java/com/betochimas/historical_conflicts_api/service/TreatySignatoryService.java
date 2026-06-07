package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.TreatySignatoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TreatySignatoryService extends CrudService<TreatySignatoryDto> {
    Page<TreatySignatoryDto> findByTreatyId(Long treatyId, Pageable pageable);
    Page<TreatySignatoryDto> findByNationId(Long nationId, Pageable pageable);
}
