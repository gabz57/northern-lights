package io.northernlights.api.core.infrastructure.security.jwt;

public interface TokenProvider {
    JwtToken of(String authToken);
}
