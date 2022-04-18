package io.northernlights.security;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
