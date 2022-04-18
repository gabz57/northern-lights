package io.northernlights.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class NorthernLightsServerSecurityContextRepository implements ServerSecurityContextRepository {

    private final ReactiveAuthenticationManager authenticationManager;
    private final ServerAuthenticationConverter northernLightsAuthenticationConverter;

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        return northernLightsAuthenticationConverter.convert(serverWebExchange)
            .flatMap(authentication -> this.authenticationManager.authenticate(authentication)
                .<SecurityContext>map(SecurityContextImpl::new))
            .onErrorMap(AuthenticationException.class, exception -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage(), exception));
    }
}
