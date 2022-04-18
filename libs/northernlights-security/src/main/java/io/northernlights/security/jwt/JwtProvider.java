package io.northernlights.security.jwt;

public interface JwtProvider {
    JwtToken of(String authToken);
}
