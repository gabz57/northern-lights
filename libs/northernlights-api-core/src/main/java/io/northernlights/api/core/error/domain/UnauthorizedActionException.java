package io.northernlights.api.core.error.domain;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(Throwable cause) {
        super(cause);
    }
}
