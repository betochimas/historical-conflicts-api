package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.domain.dto.TreatySignatoryDto;
import com.betochimas.historical_conflicts_api.service.TreatySignatoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/treaty-signatories")
@Tag(name = "Treaty Signatories", description = "Manage nation signatories of treaties, with optional filter by treaty or nation")
public class TreatySignatoryController
        extends AbstractCrudController<TreatySignatoryDto, TreatySignatoryService> {

    public TreatySignatoryController(TreatySignatoryService signatoryService) {
        super(signatoryService);
    }

    @GetMapping
    public ResponseEntity<Page<TreatySignatoryDto>> list(
            @RequestParam(required = false) Long treatyId,
            @RequestParam(required = false) Long nationId,
            Pageable pageable) {
        Page<TreatySignatoryDto> results;
        if (treatyId != null) {
            results = service.findByTreatyId(treatyId, pageable);
        } else if (nationId != null) {
            results = service.findByNationId(nationId, pageable);
        } else {
            results = service.findAll(pageable);
        }
        return ResponseEntity.ok(results);
    }
}
