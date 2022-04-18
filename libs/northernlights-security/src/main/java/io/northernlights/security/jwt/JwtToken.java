package io.northernlights.security.jwt;

import java.time.ZonedDateTime;

public interface JwtToken {

    <T> T readClaim(String name, Class<T> type, T defaultValue);

    boolean isExpired(ZonedDateTime now);

    String getUid();

    class NorthernLightsJwtToken implements JwtToken {

        private final JwtToken delegate;

        public NorthernLightsJwtToken(JwtToken delegate) {
            this.delegate = delegate;
        }

        public boolean isExpired(ZonedDateTime now) {
            return delegate.isExpired(now);
        }

        public String getUid() {
            return delegate.getUid();
        }

        public <T> T readClaim(String name, Class<T> type, T defaultValue) {
            return delegate.readClaim(name, type, defaultValue);
        }

    //    public <T> List<T> readClaimList(String name, Class<T> type) {
    //        return delegate.readClaimList(name, type);
    //    }
    }
}
