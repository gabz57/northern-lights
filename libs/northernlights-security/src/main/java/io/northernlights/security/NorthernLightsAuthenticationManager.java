package io.northernlights.security;

import io.northernlights.chat.domain.model.user.AuthOrigin;
import io.northernlights.chat.domain.store.user.UserStore;
import io.northernlights.security.jwt.JwtToken;
import io.northernlights.security.jwt.JwtTokenNorthernLightsAuthenticationAdapter;
import io.northernlights.security.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
public class NorthernLightsAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;
    private final JwtTokenNorthernLightsAuthenticationAdapter jwtTokenNorthernLightsAuthenticationAdapter = new JwtTokenNorthernLightsAuthenticationAdapter();
    private final UserStore userStore;

    public NorthernLightsAuthenticationManager(JwtProvider jwtProvider, UserStore userStore) {
        this.jwtProvider = jwtProvider;
        this.userStore = userStore;
    }

    public Mono<Authentication> authenticate(Authentication authentication) {
        return Optional.of(authentication.getCredentials())
            .map(Object::toString)
            .map(jwtProvider::of)
            .map(jwtToken -> authenticateNorthernLightsUser(jwtToken)
                .switchIfEmpty(notSubscribedUser(jwtToken)))
            .orElse(Mono.empty());
    }

    private Mono<Authentication> authenticateNorthernLightsUser(JwtToken jwtToken) {
        return userStore.findByExternalId(AuthOrigin.GOOGLE, jwtToken.getUid())
            .map(user -> jwtTokenNorthernLightsAuthenticationAdapter.buildNorthernLightsPrincipal(jwtToken, user))
            .map(principal -> new NorthernLightsAuthentication(principal, jwtToken))
            .flatMap(auth -> Mono.just(auth)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)));
    }

    private Mono<Authentication> notSubscribedUser(JwtToken jwtToken) {
        return Mono.just(new NorthernLightsAuthentication(jwtTokenNorthernLightsAuthenticationAdapter.buildNotSubscribedUser(jwtToken), jwtToken))
            .flatMap(auth -> Mono.just(auth)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)));
    }
}
