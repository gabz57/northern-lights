package io.northernlights.security;

import io.northernlights.commons.TimeService;
import io.northernlights.multitenant.ReactiveTenantContextHolder;
import io.northernlights.security.jwt.JwtToken;
import io.northernlights.security.jwt.JwtTokenNorthernLightsAuthenticationAdapter;
import io.northernlights.security.jwt.TokenProvider;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
public class NorthernLightsAuthenticationManager implements ReactiveAuthenticationManager {

    private final TokenProvider tokenProvider;
    private final TimeService timeService;
    private final JwtTokenNorthernLightsAuthenticationAdapter jwtTokenNorthernLightsAuthenticationAdapter = new JwtTokenNorthernLightsAuthenticationAdapter();

    public NorthernLightsAuthenticationManager(TokenProvider tokenProvider, TimeService timeService) {
        this.tokenProvider = tokenProvider;
        this.timeService = timeService;
    }

    public Mono<Authentication> authenticate(Authentication authentication) {
        return Optional.of(authentication.getCredentials())
            .map(Object::toString)
            .map(tokenProvider::of)
            .filter(jwtToken -> !jwtToken.isExpired(timeService.now()))
            .<Authentication>map(jwtToken -> {
                NorthernLightsPrincipal northernLightsPrincipal1 = jwtTokenNorthernLightsAuthenticationAdapter.buildNorthernLightsPrincipal(jwtToken);
                return new NorthernLightsAuthentication(northernLightsPrincipal1, jwtToken);
            }).map(data -> Mono.just(data)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(data)))
            .orElseGet(Mono::empty);
    }

    @EqualsAndHashCode(callSuper = true)
    public static class NorthernLightsAuthentication extends AbstractAuthenticationToken implements AuthenticatedPrincipal {

        private final NorthernLightsPrincipal northernLightsPrincipal;
        private transient JwtToken jwtToken;

        public NorthernLightsAuthentication(NorthernLightsPrincipal northernLightsPrincipal, JwtToken jwtToken) {
            super(grantedAuthorities(northernLightsPrincipal));
            this.northernLightsPrincipal = northernLightsPrincipal;
            this.jwtToken = jwtToken;
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
            return jwtToken.getUid();
        }

        @Override
        public JwtToken getCredentials() {
            return jwtToken;
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
            jwtToken = null;
        }
    }
}
