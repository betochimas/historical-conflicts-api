package com.betochimas.historical_conflicts_api.config;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String field, String value) {
        super(field + " already exists: " + value);
    }
}
