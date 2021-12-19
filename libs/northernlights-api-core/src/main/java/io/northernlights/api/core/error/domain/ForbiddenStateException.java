package io.northernlights.api.core.error.domain;

public class ForbiddenStateException extends RuntimeException {
    public ForbiddenStateException(String message) {
        super(message);
    }
}
