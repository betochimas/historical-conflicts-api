package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.service.BattleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/battles")
@Tag(name = "Battles", description = "Manage battles within conflicts, with optional filter by conflict")
public class BattleController extends AbstractCrudController<BattleDto, BattleService> {

    public BattleController(BattleService battleService) {
        super(battleService);
    }

    @GetMapping
    public ResponseEntity<Page<BattleDto>> list(
            @RequestParam(required = false) Long conflictId,
            @RequestParam(required = false) Long theaterId,
            Pageable pageable) {
        Page<BattleDto> results;
        if (theaterId != null) {
            results = service.findByTheaterId(theaterId, pageable);
        } else if (conflictId != null) {
            results = service.findByConflictId(conflictId, pageable);
        } else {
            results = service.findAll(pageable);
        }
        return ResponseEntity.ok(results);
    }
}
