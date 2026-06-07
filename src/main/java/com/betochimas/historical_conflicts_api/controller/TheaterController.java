package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.TheaterDto;
import com.betochimas.historical_conflicts_api.service.TheaterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/theaters")
@Tag(name = "Theaters", description = "Manage theaters of a conflict and the battles within them, with optional filter by conflict")
public class TheaterController extends AbstractCrudController<TheaterDto, TheaterService> {

    public TheaterController(TheaterService theaterService) {
        super(theaterService);
    }

    @GetMapping
    public ResponseEntity<Page<TheaterDto>> list(
            @RequestParam(required = false) Long conflictId,
            Pageable pageable) {
        Page<TheaterDto> results = conflictId != null
                ? service.findByConflictId(conflictId, pageable)
                : service.findAll(pageable);
        return ResponseEntity.ok(results);
    }
}
