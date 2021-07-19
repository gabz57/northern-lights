package io.northernlights.chat.api.infrastructure;

import io.northernlights.api.core.infrastructure.security.NorthernLightsPrincipal;
import io.northernlights.commons.TimeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import static java.util.TimeZone.getTimeZone;
import static java.util.TimeZone.setDefault;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
@EnableWebFlux
public class ChatApiConfiguration implements WebFluxConfigurer {

    private static final ZoneId ZONE_ID = ZoneOffset.UTC;
    private static final Clock CLOCK = Clock.system(ZONE_ID);

    @PostConstruct
    public void setUpDefaultZoneId() {
        setDefault(getTimeZone(ZONE_ID));
    }

    @Bean
    public TimeService timeService() {
        return () -> ZonedDateTime.now(CLOCK);
    }

    @Bean
    @ConfigurationProperties("chat-api")
    public ChatApiProperties chatApiProperties() {
        return new ChatApiProperties();
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(256 * 1024);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico")
            .addResourceLocations("/public", "classpath:/static/")
            .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
//        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.featuresToEnable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//    }

    @Bean
    public ReactiveAuditorAware<String> auditorAware() {
        return () -> ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .cast(NorthernLightsPrincipal.class)
            .map(NorthernLightsPrincipal::getUid);
    }

    @Bean
    public RouterFunction<ServerResponse> indexRouter(@Value("classpath:/static/index.html") final Resource indexHtml) {
        // Serve static index.html at root, for convenient message publishing.
        return route(RequestPredicates.GET("/"),
            request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml));
    }
}
