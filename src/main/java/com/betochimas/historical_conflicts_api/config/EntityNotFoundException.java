package com.betochimas.historical_conflicts_api.config;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityType, Long id) {
        super(entityType + " not found: " + id);
    }
}
