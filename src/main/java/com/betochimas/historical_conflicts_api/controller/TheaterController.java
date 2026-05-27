package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.TheaterDto;
import com.betochimas.historical_conflicts_api.service.TheaterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/theaters")
@Tag(name = "Theaters", description = "Manage theaters of a conflict and the battles within them, with optional filter by conflict")
public class TheaterController {

    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @PostMapping
    public ResponseEntity<TheaterDto> createTheater(@Valid @RequestBody TheaterDto theaterDto) {
        return new ResponseEntity<>(theaterService.create(theaterDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<TheaterDto>> listTheaters(
            @RequestParam(required = false) Long conflictId,
            Pageable pageable) {
        Page<TheaterDto> results = conflictId != null
                ? theaterService.findByConflictId(conflictId, pageable)
                : theaterService.findAll(pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheaterDto> getTheater(@PathVariable Long id) {
        return theaterService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheaterDto> fullUpdateTheater(
            @PathVariable Long id,
            @Valid @RequestBody TheaterDto theaterDto) {
        if (!theaterService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(theaterService.fullUpdate(id, theaterDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TheaterDto> partialUpdateTheater(
            @PathVariable Long id,
            @RequestBody TheaterDto theaterDto) {
        return theaterService.partialUpdate(id, theaterDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheater(@PathVariable Long id) {
        theaterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
