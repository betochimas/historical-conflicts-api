package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictAtlasDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.service.ConflictAtlasService;
import com.betochimas.historical_conflicts_api.service.ConflictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conflicts")
@Tag(name = "Conflicts", description = "Manage named wars, civil wars, rebellions, and other conflicts")
public class ConflictController extends AbstractCrudController<ConflictDto, ConflictService> {

    private final ConflictAtlasService conflictAtlasService;

    public ConflictController(ConflictService conflictService,
                              ConflictAtlasService conflictAtlasService) {
        super(conflictService);
        this.conflictAtlasService = conflictAtlasService;
    }

    @GetMapping
    public ResponseEntity<Page<ConflictDto>> list(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}/atlas")
    @Operation(summary = "Aggregate atlas for a conflict",
            description = "Everything the interactive map + timeline needs in one payload: "
                    + "the conflict, its theaters, its date-sorted battles (with coordinates), "
                    + "its participants (with nation names), and computed stats. 404 if absent.")
    public ResponseEntity<ConflictAtlasDto> getConflictAtlas(@PathVariable Long id) {
        return ResponseEntity.ok(conflictAtlasService.getAtlas(id));
    }
}
