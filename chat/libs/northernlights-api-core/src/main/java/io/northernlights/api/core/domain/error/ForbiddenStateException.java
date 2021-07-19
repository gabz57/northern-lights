package io.northernlights.api.core.domain.error;

public class ForbiddenStateException extends RuntimeException {
    public ForbiddenStateException(String message) {
        super(message);
    }
}
