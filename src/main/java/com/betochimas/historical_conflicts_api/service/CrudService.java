package com.betochimas.historical_conflicts_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * The CRUD contract shared by every domain service: create, paginated list, read-one, existence
 * check, full/partial update, delete. Resource-specific finders (e.g. {@code findByConflictId})
 * live on the concrete sub-interfaces. Paired with
 * {@link com.betochimas.historical_conflicts_api.controller.AbstractCrudController}.
 *
 * @param <D> the resource DTO (request + response body)
 */
public interface CrudService<D> {
    D create(D dto);
    Page<D> findAll(Pageable pageable);
    Optional<D> findOne(Long id);
    boolean isExists(Long id);
    D fullUpdate(Long id, D dto);
    Optional<D> partialUpdate(Long id, D dto);
    void delete(Long id);
}
