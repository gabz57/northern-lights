package io.northernlights.security.jwt.auth0;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.northernlights.security.jwt.JwtToken;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Auth0NorthernLightsJwtToken implements JwtToken {

    private final DecodedJWT jwt;

    public Auth0NorthernLightsJwtToken(DecodedJWT jwt) {
        this.jwt = jwt;
    }

    public boolean isExpired(ZonedDateTime now) {
        return Date.from(now.toInstant()).after(jwt.getExpiresAt());
    }

    public String getUid() {
        return jwt.getClaim("sub").asString();
    }

    public String getName() {
        return jwt.getClaim("name").asString();
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
