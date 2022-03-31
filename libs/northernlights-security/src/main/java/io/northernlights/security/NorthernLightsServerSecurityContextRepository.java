package io.northernlights.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class NorthernLightsServerSecurityContextRepository implements ServerSecurityContextRepository {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final ReactiveAuthenticationManager authenticationManager;

    public NorthernLightsServerSecurityContextRepository(ReactiveAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        return Mono.empty();
//        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        return Optional.of(serverWebExchange.getRequest().getHeaders())
            .map(headers -> headers.getFirst(HttpHeaders.AUTHORIZATION))
            .filter(authHeader -> authHeader.startsWith(TOKEN_PREFIX))
            .map(authHeader -> authHeader.replace(TOKEN_PREFIX, ""))
            .map(authToken -> new UsernamePasswordAuthenticationToken(authToken, authToken)) // used with NorthernLightsAuthenticationManager
            .map(authentication -> this.authenticationManager.authenticate(authentication)
                .map(SecurityContextImpl::new)
                .cast(SecurityContext.class))
            .orElseGet(Mono::empty);
    }
}