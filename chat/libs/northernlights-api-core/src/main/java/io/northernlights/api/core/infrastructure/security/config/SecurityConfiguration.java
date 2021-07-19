package io.northernlights.api.core.infrastructure.security.config;

import io.northernlights.api.core.infrastructure.security.NorthernLightsAuthenticationManager;
import io.northernlights.api.core.infrastructure.security.SecurityContextRepository;
import io.northernlights.api.core.infrastructure.security.jwt.TokenProvider;
import io.northernlights.api.core.infrastructure.security.jwt.auth0.Auth0TokenProvider;
import io.northernlights.commons.TimeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ServerSecurityContextRepository securityContextRepository) {

        http.csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .logout().disable()
            .securityContextRepository(securityContextRepository)
            .exceptionHandling().authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED));

        http.authorizeExchange()
            .pathMatchers("/favicon.ico", "/css/**", "/webjars/**", "/webjars/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
//            // custom products
//            .pathMatchers(GET, CUSTOM_PRODUCTS).access(InvivoPrincipal::canReadCustomProducts)
//            .pathMatchers(GET, CUSTOM_PRODUCTS + "/*").access(InvivoPrincipal::canReadCustomProducts)
//            .pathMatchers(PUT, CUSTOM_PRODUCTS + "/{" + PRODUCT_ID + "}").access(InvivoPrincipal::canManageCustomProducts)
//            .pathMatchers(PUT, CUSTOM_PRODUCTS).access(InvivoPrincipal::canManageCustomProducts)
//            // indexing requests
//            .pathMatchers(POST, PRODUCT_INDEX + "/products/{" + PARTNER_ID + "}").access(InvivoPrincipal::canManagePartnerIndex)
//            .pathMatchers(POST, PRODUCT_INDEX + "/cooperative/{" + PARTNER_ID + "}").access(InvivoPrincipal::canManagePartnerIndex)
//            .pathMatchers(POST, PRODUCT_INDEX + "/referential").access(InvivoPrincipal::canManageInvivoReferentialIndex)
            .anyExchange().authenticated();

        return http.build();
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository(TimeService timeService) {
        TokenProvider tokenProvider = new Auth0TokenProvider();
        ReactiveAuthenticationManager authenticationManager = new NorthernLightsAuthenticationManager(tokenProvider, timeService);
        return new SecurityContextRepository(authenticationManager);
    }
}
