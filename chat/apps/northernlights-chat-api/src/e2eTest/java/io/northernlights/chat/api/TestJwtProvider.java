package io.northernlights.chat.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.northernlights.security.AuthenticationException;
import io.northernlights.security.jwt.JwtProvider;
import io.northernlights.security.jwt.JwtToken;
import io.northernlights.security.jwt.auth0.Auth0NorthernLightsJwtToken;

import java.time.ZonedDateTime;

public class TestJwtProvider implements JwtProvider {

    @Override
    public JwtToken of(String authToken) {
        DecodedJWT jwt;
        try {
            jwt = JWT.decode(authToken);
        } catch (JWTVerificationException e) {
            throw new AuthenticationException("Failed to decode authentication token", e);
        }

        return new JwtToken.NorthernLightsJwtToken(new TestJwtToken(jwt));
    }

    public static class TestJwtToken implements JwtToken {

        private final Auth0NorthernLightsJwtToken jwt;
        public TestJwtToken(DecodedJWT jwt) {
            this.jwt = new Auth0NorthernLightsJwtToken(jwt);
        }

        @Override
        public <T> T readClaim(String name, Class<T> type, T defaultValue) {
            return jwt.readClaim(name, type, defaultValue);
        }

        @Override
        public boolean isExpired(ZonedDateTime now) {
            return false;
        }

        @Override
        public String getUid() {
            return jwt.getUid();
        }

        @Override
        public String getName() {
            return jwt.getName();
        }
    }
}
