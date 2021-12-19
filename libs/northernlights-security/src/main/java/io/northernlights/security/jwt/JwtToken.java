package io.northernlights.security.jwt;

import java.time.ZonedDateTime;

public interface JwtToken {

    <T> T readClaim(String name, Class<T> type, T defaultValue);

    boolean isExpired(ZonedDateTime now);

    String getUid();
}
