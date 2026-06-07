package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import com.betochimas.historical_conflicts_api.service.ConflictParticipantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conflict-participants")
@Tag(name = "Conflict Participants", description = "Manage nation participation in conflicts (role, troops, casualties)")
public class ConflictParticipantController
        extends AbstractCrudController<ConflictParticipantDto, ConflictParticipantService> {

    public ConflictParticipantController(ConflictParticipantService participantService) {
        super(participantService);
    }

    @GetMapping
    public ResponseEntity<Page<ConflictParticipantDto>> list(
            @RequestParam(required = false) Long conflictId,
            Pageable pageable) {
        Page<ConflictParticipantDto> results = conflictId != null
                ? service.findByConflictId(conflictId, pageable)
                : service.findAll(pageable);
        return ResponseEntity.ok(results);
    }
}
