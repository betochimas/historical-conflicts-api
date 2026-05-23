package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import com.betochimas.historical_conflicts_api.service.ConflictParticipantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conflict-participants")
@Tag(name = "Conflict Participants", description = "Manage nation participation in conflicts (role, troops, casualties)")
public class ConflictParticipantController {

    private final ConflictParticipantService participantService;

    public ConflictParticipantController(ConflictParticipantService participantService) {
        this.participantService = participantService;
    }

    @PostMapping
    public ResponseEntity<ConflictParticipantDto> createParticipant(
            @RequestBody ConflictParticipantDto dto) {
        return new ResponseEntity<>(participantService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ConflictParticipantDto>> listParticipants(
            @RequestParam(required = false) Long conflictId,
            Pageable pageable) {
        Page<ConflictParticipantDto> results = conflictId != null
                ? participantService.findByConflictId(conflictId, pageable)
                : participantService.findAll(pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConflictParticipantDto> getParticipant(@PathVariable Long id) {
        return participantService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConflictParticipantDto> fullUpdateParticipant(
            @PathVariable Long id,
            @RequestBody ConflictParticipantDto dto) {
        if (!participantService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(participantService.fullUpdate(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ConflictParticipantDto> partialUpdateParticipant(
            @PathVariable Long id,
            @RequestBody ConflictParticipantDto dto) {
        return participantService.partialUpdate(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        participantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
