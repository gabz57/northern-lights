package io.northernlights.chat.api.infrastructure;

import io.northernlights.chat.store.user.UserStore;
import io.northernlights.commons.TimeService;
import io.northernlights.security.NorthernLightsAuthenticationConverter;
import io.northernlights.security.NorthernLightsAuthenticationManager;
import io.northernlights.security.NorthernLightsServerSecurityContextRepository;
import io.northernlights.security.jwt.JwtProvider;
import io.northernlights.security.jwt.auth0.Auth0GoogleJwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import static io.northernlights.chat.domain.ApiConstants.*;
import static io.northernlights.security.NorthernLightsRoles.RoleType.CHATTER;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {

    private static final ZoneId ZONE_ID = ZoneOffset.UTC;
    private static final Clock CLOCK = Clock.system(ZONE_ID);

    @PostConstruct
    public void setUpDefaultZoneId() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZONE_ID));
    }

    @ConditionalOnMissingBean
    @Bean
    public TimeService timeService() {
        return () -> ZonedDateTime.now(CLOCK);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ServerSecurityContextRepository securityContextRepository) {

        return http.csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .logout().disable()
            .securityContextRepository(securityContextRepository)
//            .oauth2Client()
//                .authenticationConverter(new NorthernLightsAuthenticationConverter())
//                .authenticationManager(authenticationManager)
//            .and()
//            .exceptionHandling().authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
//        .and()

            .authorizeExchange(authorize -> authorize
                .pathMatchers("/", "/favicon.ico", "/index.html", "/eventsource.js", "/css/**", "/webjars/**", "/webjars/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                .pathMatchers(OPTIONS).permitAll()
                .pathMatchers(GET, CHAT_API_SSE).hasRole(CHATTER.type)
                .pathMatchers(CHAT_API_ALL).hasRole(CHATTER.type)
                .pathMatchers(USER_API_INFO).hasRole(CHATTER.type)
                .pathMatchers(USER_API_SUBSCRIBE).authenticated()
                .anyExchange().authenticated())
            .build();
    }

    @Bean
    public JwtProvider jwtProvider(TimeService timeService, @Value("${chat.api.google-client-id}") String googleClientId) {
        return new Auth0GoogleJwtProvider(timeService, googleClientId);
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(UserStore userStore, JwtProvider jwtProvider) {
        return new NorthernLightsAuthenticationManager(jwtProvider, userStore);
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository(ReactiveAuthenticationManager reactiveAuthenticationManager) {
        return new NorthernLightsServerSecurityContextRepository(reactiveAuthenticationManager, new NorthernLightsAuthenticationConverter());
    }

    @Bean
    public WebFilter corsResponseHeadersWebFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            exchange.getResponse()
                .getHeaders()
                .add(ACCESS_CONTROL_ALLOW_HEADERS, "*");
            return chain.filter(exchange);
        };
    }
//
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration corsConfig = new CorsConfiguration();
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfig);
//        return new CorsWebFilter(source);
//    }
}
