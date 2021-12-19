package io.northernlights.security.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class ServerHttpJwtAuthenticationConverter implements ServerAuthenticationConverter {

    private static final String TOKEN_PREFIX = "Bearer ";

    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Optional.of(exchange.getRequest().getHeaders())
            .map(headers -> headers.getFirst(HttpHeaders.AUTHORIZATION))
            .filter(authHeader -> authHeader.startsWith(TOKEN_PREFIX))
            .map(authHeader -> authHeader.replace(TOKEN_PREFIX, ""))
            // used with NorthernLightsAuthenticationManager
            .map(authToken -> (Authentication) new UsernamePasswordAuthenticationToken(authToken, authToken))
            .map(Mono::just)
            .orElseGet(Mono::empty);
    }
}
