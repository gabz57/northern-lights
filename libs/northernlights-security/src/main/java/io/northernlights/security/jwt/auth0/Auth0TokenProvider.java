package io.northernlights.security.jwt.auth0;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.northernlights.security.jwt.JwtToken;
import io.northernlights.security.jwt.TokenProvider;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Auth0TokenProvider implements TokenProvider {

    public JwtToken of(String authToken) {
        return new NorthernLightsAuth0JwtToken(authToken);
    }

    public static class NorthernLightsAuth0JwtToken implements JwtToken {

        private final DecodedJWT jwt;

        public NorthernLightsAuth0JwtToken(String authToken) {
            this.jwt = JWT.decode(authToken);
        }

        public boolean isExpired(ZonedDateTime now) {
            return Date.from(now.toInstant()).after(jwt.getExpiresAt());
        }

        public String getUid() {
            return jwt.getClaim("sub").asString();
        }

        public <T> T readClaim(String name, Class<T> type, T defaultValue) {
            return Optional.of(jwt.getClaim(name))
                .filter(claim -> !claim.isNull())
                .map(claim -> claim.as(type))
                .orElse(defaultValue);
        }

        public <T> List<T> readClaimList(String name, Class<T> type) {
            return Optional.of(jwt.getClaim(name))
                .filter(claim -> !claim.isNull())
                .map(claim -> claim.asList(type))
                .orElse(null);
        }
    }
}
