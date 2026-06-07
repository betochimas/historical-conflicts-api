package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.AllianceMemberDto;
import com.betochimas.historical_conflicts_api.service.AllianceMemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alliance-members")
@Tag(name = "Alliance Members", description = "Manage nation membership in alliances, with optional filter by alliance or nation")
public class AllianceMemberController
        extends AbstractCrudController<AllianceMemberDto, AllianceMemberService> {

    public AllianceMemberController(AllianceMemberService memberService) {
        super(memberService);
    }

    @GetMapping
    public ResponseEntity<Page<AllianceMemberDto>> list(
            @RequestParam(required = false) Long allianceId,
            @RequestParam(required = false) Long nationId,
            Pageable pageable) {
        Page<AllianceMemberDto> results;
        if (allianceId != null) {
            results = service.findByAllianceId(allianceId, pageable);
        } else if (nationId != null) {
            results = service.findByNationId(nationId, pageable);
        } else {
            results = service.findAll(pageable);
        }
        return ResponseEntity.ok(results);
    }
}
