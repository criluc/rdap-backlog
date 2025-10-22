package it.nic.rdap.service;

import java.time.Duration;

public class RdapRateLimitException extends RuntimeException {
    private final Duration retryAfter;

    public RdapRateLimitException(String message, Duration retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

    public Duration retryAfter() {
        return retryAfter;
    }
}
