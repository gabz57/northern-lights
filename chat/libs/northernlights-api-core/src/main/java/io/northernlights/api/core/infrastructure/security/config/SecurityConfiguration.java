package io.northernlights.api.core.infrastructure.security.config;

import io.northernlights.api.core.infrastructure.security.DummyNorthernLightsAuthenticationManager;
import io.northernlights.api.core.infrastructure.security.SecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

import static org.springframework.http.HttpMethod.GET;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfiguration {

    public static final String CHAT_CONVERSATION = "/v1/chat/api";

//    private static final ZoneId ZONE_ID = ZoneOffset.UTC;
//    private static final Clock CLOCK = Clock.system(ZONE_ID);
//
//    @PostConstruct
//    public void setUpDefaultZoneId() {
//        setDefault(getTimeZone(ZONE_ID));
//    }
//
//    @ConditionalOnMissingBean
//    @Bean
//    public TimeService timeService() {
//        return () -> ZonedDateTime.now(CLOCK);
//    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ServerSecurityContextRepository securityContextRepository) {

        return http.csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .logout().disable()
            .securityContextRepository(securityContextRepository)
//            .exceptionHandling().authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
//        .and()

            .authorizeExchange()
            .pathMatchers("/", "/favicon.ico", "/index.html", "/eventsource.js", "/css/**", "/webjars/**", "/webjars/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
//
            .pathMatchers(GET, CHAT_CONVERSATION + "/sse").permitAll()
            .pathMatchers(CHAT_CONVERSATION + "/**").authenticated()
            .anyExchange().authenticated().and().build();

    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository(/*TimeService timeService*/) {
//        TokenProvider tokenProvider = new Auth0TokenProvider();
//        ReactiveAuthenticationManager authenticationManager = new NorthernLightsAuthenticationManager(tokenProvider, timeService);
        ReactiveAuthenticationManager authenticationManager = new DummyNorthernLightsAuthenticationManager();
        return new SecurityContextRepository(authenticationManager);
    }
}
