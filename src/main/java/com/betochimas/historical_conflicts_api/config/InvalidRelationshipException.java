package com.betochimas.historical_conflicts_api.config;

/**
 * Thrown when a write would create an inconsistent relationship between entities
 * (e.g. assigning a battle to a theater that belongs to a different conflict).
 * Mapped to HTTP 400 by {@link GlobalExceptionHandler}.
 */
public class InvalidRelationshipException extends RuntimeException {
    public InvalidRelationshipException(String message) {
        super(message);
    }
}
