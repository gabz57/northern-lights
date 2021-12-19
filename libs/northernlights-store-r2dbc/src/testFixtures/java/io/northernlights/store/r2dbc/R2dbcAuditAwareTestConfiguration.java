package io.northernlights.store.r2dbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import reactor.core.publisher.Mono;

@Configuration
@EnableR2dbcAuditing
public class R2dbcAuditAwareTestConfiguration {

    /**
     * // In application, declare the following implementation to map JWT token 'uid' into DB <br/>
     * <br/>
     * {@code
     * <p>
     * return () -> ReactiveSecurityContextHolder.getContext()
     * .map(SecurityContext::getAuthentication)
     * .filter(Authentication::isAuthenticated)
     * .map(Authentication::getPrincipal)
     * .cast(NorthernLightsPrincipal.class)
     * .map(NorthernLightsPrincipal::getUid);
     * }
     */
    @Bean
    public ReactiveAuditorAware<String> auditorAware() {
        return () -> Mono.just("");
    }

}
