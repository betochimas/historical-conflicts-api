package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.service.NationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nations")
@Tag(name = "Nations", description = "Manage independent nations referenced by conflicts and participants")
public class NationController {

    private final NationService nationService;

    public NationController(NationService nationService) {
        this.nationService = nationService;
    }

    @PostMapping
    public ResponseEntity<NationDto> createNation(@RequestBody NationDto nationDto) {
        NationDto created = nationService.create(nationDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<NationDto>> listNations(Pageable pageable) {
        return ResponseEntity.ok(nationService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NationDto> getNation(@PathVariable Long id) {
        return nationService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NationDto> fullUpdateNation(
            @PathVariable Long id,
            @RequestBody NationDto nationDto) {
        if (!nationService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(nationService.fullUpdate(id, nationDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<NationDto> partialUpdateNation(
            @PathVariable Long id,
            @RequestBody NationDto nationDto) {
        return nationService.partialUpdate(id, nationDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNation(@PathVariable Long id) {
        nationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}