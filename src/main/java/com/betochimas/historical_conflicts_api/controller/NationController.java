package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.service.NationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nations")
@Tag(name = "Nations", description = "Manage independent nations referenced by conflicts and participants")
public class NationController extends AbstractCrudController<NationDto, NationService> {

    public NationController(NationService nationService) {
        super(nationService);
    }

    @GetMapping
    public ResponseEntity<Page<NationDto>> list(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }
}
