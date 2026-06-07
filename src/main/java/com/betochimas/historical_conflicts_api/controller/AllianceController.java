package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.AllianceDto;
import com.betochimas.historical_conflicts_api.service.AllianceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alliances")
@Tag(name = "Alliances", description = "Manage standing alliances and coalitions of nations")
public class AllianceController extends AbstractCrudController<AllianceDto, AllianceService> {

    public AllianceController(AllianceService allianceService) {
        super(allianceService);
    }

    @GetMapping
    public ResponseEntity<Page<AllianceDto>> list(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }
}
