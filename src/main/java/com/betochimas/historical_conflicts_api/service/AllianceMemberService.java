package com.betochimas.historical_conflicts_api.service;

import com.betochimas.historical_conflicts_api.domain.dto.AllianceMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AllianceMemberService extends CrudService<AllianceMemberDto> {
    Page<AllianceMemberDto> findByAllianceId(Long allianceId, Pageable pageable);
    Page<AllianceMemberDto> findByNationId(Long nationId, Pageable pageable);
}
