package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.LeaderDto;
import com.betochimas.historical_conflicts_api.service.LeaderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaders")
@Tag(name = "Leaders", description = "Manage historical leaders tied to a nation, with optional filter by nation")
public class LeaderController {

    private final LeaderService leaderService;

    public LeaderController(LeaderService leaderService) {
        this.leaderService = leaderService;
    }

    @PostMapping
    public ResponseEntity<LeaderDto> createLeader(@Valid @RequestBody LeaderDto leaderDto) {
        return new ResponseEntity<>(leaderService.create(leaderDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<LeaderDto>> listLeaders(
            @RequestParam(required = false) Long nationId,
            Pageable pageable) {
        Page<LeaderDto> results = nationId != null
                ? leaderService.findByNationId(nationId, pageable)
                : leaderService.findAll(pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaderDto> getLeader(@PathVariable Long id) {
        return leaderService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaderDto> fullUpdateLeader(
            @PathVariable Long id,
            @Valid @RequestBody LeaderDto leaderDto) {
        if (!leaderService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(leaderService.fullUpdate(id, leaderDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LeaderDto> partialUpdateLeader(
            @PathVariable Long id,
            @RequestBody LeaderDto leaderDto) {
        return leaderService.partialUpdate(id, leaderDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeader(@PathVariable Long id) {
        leaderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
