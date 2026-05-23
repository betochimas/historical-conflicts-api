package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.service.BattleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles")
@Tag(name = "Battles", description = "Manage battles within conflicts, with optional filter by conflict")
public class BattleController {

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping
    public ResponseEntity<BattleDto> createBattle(@RequestBody BattleDto battleDto) {
        return new ResponseEntity<>(battleService.create(battleDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<BattleDto>> listBattles(
            @RequestParam(required = false) Long conflictId,
            Pageable pageable) {
        Page<BattleDto> results = conflictId != null
                ? battleService.findByConflictId(conflictId, pageable)
                : battleService.findAll(pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BattleDto> getBattle(@PathVariable Long id) {
        return battleService.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BattleDto> fullUpdateBattle(
            @PathVariable Long id,
            @RequestBody BattleDto battleDto) {
        if (!battleService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(battleService.fullUpdate(id, battleDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BattleDto> partialUpdateBattle(
            @PathVariable Long id,
            @RequestBody BattleDto battleDto) {
        return battleService.partialUpdate(id, battleDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBattle(@PathVariable Long id) {
        battleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
