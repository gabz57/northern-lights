package io.northernlights.security.jwt;

import io.northernlights.security.jwt.JwtToken;

public interface TokenProvider {
    JwtToken of(String authToken);
}
