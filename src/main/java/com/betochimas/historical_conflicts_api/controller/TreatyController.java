package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.TreatyDto;
import com.betochimas.historical_conflicts_api.service.TreatyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/treaties")
@Tag(name = "Treaties", description = "Manage treaties, optionally tied to a conflict, with optional filter by conflict")
public class TreatyController extends AbstractCrudController<TreatyDto, TreatyService> {

    public TreatyController(TreatyService treatyService) {
        super(treatyService);
    }

    @GetMapping
    public ResponseEntity<Page<TreatyDto>> list(
            @RequestParam(required = false) Long conflictId,
            Pageable pageable) {
        Page<TreatyDto> results = conflictId != null
                ? service.findByConflictId(conflictId, pageable)
                : service.findAll(pageable);
        return ResponseEntity.ok(results);
    }
}
