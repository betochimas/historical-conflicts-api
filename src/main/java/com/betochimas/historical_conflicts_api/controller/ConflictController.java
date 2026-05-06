package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.service.ConflictService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conflicts")
public class ConflictController {

    private final ConflictService conflictService;

    public ConflictController(ConflictService conflictService) {
        this.conflictService = conflictService;
    }

    @PostMapping
    public ResponseEntity<ConflictDto> createConflict(@RequestBody ConflictDto conflictDto) {
        return new ResponseEntity<>(conflictService.create(conflictDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ConflictDto>> listConflicts() {
        return ResponseEntity.ok(conflictService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConflictDto> getConflict(@PathVariable Long id) {
        return conflictService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConflictDto> fullUpdateConflict(
            @PathVariable Long id,
            @RequestBody ConflictDto conflictDto) {
        if (!conflictService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(conflictService.fullUpdate(id, conflictDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ConflictDto> partialUpdateConflict(
            @PathVariable Long id,
            @RequestBody ConflictDto conflictDto) {
        return conflictService.partialUpdate(id, conflictDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConflict(@PathVariable Long id) {
        conflictService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
