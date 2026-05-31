package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictAtlasDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.service.ConflictAtlasService;
import com.betochimas.historical_conflicts_api.service.ConflictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conflicts")
@Tag(name = "Conflicts", description = "Manage named wars, civil wars, rebellions, and other conflicts")
public class ConflictController {

    private final ConflictService conflictService;
    private final ConflictAtlasService conflictAtlasService;

    public ConflictController(ConflictService conflictService,
                              ConflictAtlasService conflictAtlasService) {
        this.conflictService = conflictService;
        this.conflictAtlasService = conflictAtlasService;
    }

    @PostMapping
    public ResponseEntity<ConflictDto> createConflict(@RequestBody ConflictDto conflictDto) {
        return new ResponseEntity<>(conflictService.create(conflictDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ConflictDto>> listConflicts(Pageable pageable) {
        return ResponseEntity.ok(conflictService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConflictDto> getConflict(@PathVariable Long id) {
        return conflictService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/atlas")
    @Operation(summary = "Aggregate atlas for a conflict",
            description = "Everything the interactive map + timeline needs in one payload: "
                    + "the conflict, its theaters, its date-sorted battles (with coordinates), "
                    + "its participants (with nation names), and computed stats. 404 if absent.")
    public ResponseEntity<ConflictAtlasDto> getConflictAtlas(@PathVariable Long id) {
        return ResponseEntity.ok(conflictAtlasService.getAtlas(id));
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
