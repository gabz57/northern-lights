package io.northernlights.api.core.infrastructure.security;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
public class DummyNorthernLightsAuthenticationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Optional.of(authentication.getCredentials())
            .map(Object::toString)
            .map(id -> {
                try {
                    NorthernLightsPrincipal northernLightsPrincipal = NorthernLightsPrincipal.builder()
                        .uid(id)// <=> ChatterId.id
                        .scopes(List.of())
                        .build();
                    Authentication northernLightsAuthentication = new DummyNorthernLightsAuthentication(northernLightsPrincipal);
                    SecurityContextHolder.getContext().setAuthentication(northernLightsAuthentication);
                    return northernLightsAuthentication;
                } catch (Exception e) {
                    log.error("Failed to read dummy Token", e);
                    return null;
                }
            })
            .map(Mono::just)
            // NOTE: instead of empty, one could implement an AnonymousAuthenticationToken with public collections reading scopes
            .orElseGet(Mono::empty);
    }

    @EqualsAndHashCode(callSuper = true)
    public static class DummyNorthernLightsAuthentication extends AbstractAuthenticationToken implements AuthenticatedPrincipal {

        private final NorthernLightsPrincipal northernLightsPrincipal;

        public DummyNorthernLightsAuthentication(NorthernLightsPrincipal northernLightsPrincipal) {
            super(grantedAuthorities(northernLightsPrincipal));
            this.northernLightsPrincipal = northernLightsPrincipal;
            super.setAuthenticated(true); // must use super, as we override
        }

        private static List<GrantedAuthority> grantedAuthorities(NorthernLightsPrincipal northernLightsPrincipal) {
            return northernLightsPrincipal.getScopes().stream()
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(toList());
        }

        @Override
        public String getName() {
            return northernLightsPrincipal.getUid();
        }

        @Override
        public String getCredentials() {
            return northernLightsPrincipal.getUid();
        }

        @Override
        public NorthernLightsPrincipal getPrincipal() {
            return northernLightsPrincipal;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) {
            if (isAuthenticated) {
                throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
            }
            super.setAuthenticated(false);
        }

        @Override
        public void eraseCredentials() {
            super.eraseCredentials();
        }
    }
}
