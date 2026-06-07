package com.betochimas.historical_conflicts_api.controller;

import com.betochimas.historical_conflicts_api.service.CrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Generic CRUD handlers shared by every resource controller: POST (create), GET /{id}, PUT /{id}
 * (full update), PATCH /{id} (partial update), DELETE /{id}. A concrete controller supplies the
 * class-level {@code @RequestMapping} path and {@code @Tag}, the typed service (via the
 * constructor), and any resource-specific list/filter endpoint — those vary per resource
 * ({@code ?conflictId=}, {@code ?nationId=}, {@code ?theaterId=}), so they are not declared here.
 *
 * <p>Spring detects these {@code @*Mapping} methods on the superclass and resolves the type
 * variable {@code D} for {@code @RequestBody} binding against the concrete subclass. {@code @Valid}
 * is applied to the create/full-update bodies uniformly; it is a no-op for DTOs without
 * bean-validation constraints, so this changes no existing behavior.
 *
 * @param <D> the resource DTO (request + response body)
 * @param <S> the resource service
 */
public abstract class AbstractCrudController<D, S extends CrudService<D>> {

    protected final S service;

    protected AbstractCrudController(S service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<D> create(@Valid @RequestBody D dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<D> getOne(@PathVariable Long id) {
        return service.findOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<D> fullUpdate(@PathVariable Long id, @Valid @RequestBody D dto) {
        if (!service.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.fullUpdate(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<D> partialUpdate(@PathVariable Long id, @RequestBody D dto) {
        return service.partialUpdate(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
