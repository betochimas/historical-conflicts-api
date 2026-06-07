package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.LeaderDto;
import com.betochimas.historical_conflicts_api.service.LeaderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaders")
@Tag(name = "Leaders", description = "Manage historical leaders tied to a nation, with optional filter by nation")
public class LeaderController extends AbstractCrudController<LeaderDto, LeaderService> {

    public LeaderController(LeaderService leaderService) {
        super(leaderService);
    }

    @GetMapping
    public ResponseEntity<Page<LeaderDto>> list(
            @RequestParam(required = false) Long nationId,
            Pageable pageable) {
        Page<LeaderDto> results = nationId != null
                ? service.findByNationId(nationId, pageable)
                : service.findAll(pageable);
        return ResponseEntity.ok(results);
    }
}
