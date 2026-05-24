package com.betochimas.historical_conflicts_api.config;

/**
 * Thrown when public self-registration is turned off (e.g. in the hosted demo, where the
 * shared demo account is the only write path). Mapped to HTTP 403 by {@link GlobalExceptionHandler}.
 */
public class RegistrationDisabledException extends RuntimeException {
    public RegistrationDisabledException() {
        super("Registration is disabled");
    }
}
