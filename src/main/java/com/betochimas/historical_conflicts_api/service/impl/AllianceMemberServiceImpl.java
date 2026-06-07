package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.CacheConfig;
import com.betochimas.historical_conflicts_api.domain.dto.AllianceMemberDto;
import com.betochimas.historical_conflicts_api.domain.mapper.AllianceMemberMapper;
import com.betochimas.historical_conflicts_api.repository.AllianceMemberRepository;
import com.betochimas.historical_conflicts_api.service.AllianceMemberService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AllianceMemberServiceImpl implements AllianceMemberService {

    private final AllianceMemberRepository memberRepository;
    private final AllianceMemberMapper mapper;

    public AllianceMemberServiceImpl(AllianceMemberRepository memberRepository,
                                     AllianceMemberMapper mapper) {
        this.memberRepository = memberRepository;
        this.mapper = mapper;
    }

    @Override
    public AllianceMemberDto create(AllianceMemberDto dto) {
        return mapper.toDto(memberRepository.save(mapper.toEntity(dto)));
    }

    @Override
    public Page<AllianceMemberDto> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<AllianceMemberDto> findByAllianceId(Long allianceId, Pageable pageable) {
        return memberRepository.findByAllianceId(allianceId, pageable).map(mapper::toDto);
    }

    @Override
    public Page<AllianceMemberDto> findByNationId(Long nationId, Pageable pageable) {
        return memberRepository.findByNationId(nationId, pageable).map(mapper::toDto);
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.ALLIANCE_MEMBERS, key = "#id", unless = "#result == null")
    public Optional<AllianceMemberDto> findOne(Long id) {
        return memberRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public boolean isExists(Long id) {
        return memberRepository.existsById(id);
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.ALLIANCE_MEMBERS, key = "#id")
    public AllianceMemberDto fullUpdate(Long id, AllianceMemberDto dto) {
        dto.setId(id);
        return mapper.toDto(memberRepository.save(mapper.toEntity(dto)));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.ALLIANCE_MEMBERS, key = "#id")
    public Optional<AllianceMemberDto> partialUpdate(Long id, AllianceMemberDto dto) {
        return memberRepository.findById(id).map(existing -> {
            mapper.update(existing, dto);
            return mapper.toDto(memberRepository.save(existing));
        });
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.ALLIANCE_MEMBERS, key = "#id")
    public void delete(Long id) {
        memberRepository.deleteById(id);
    }
}
