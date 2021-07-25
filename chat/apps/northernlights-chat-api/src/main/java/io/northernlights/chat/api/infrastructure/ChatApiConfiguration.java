package io.northernlights.chat.api.infrastructure;

import io.northernlights.api.core.infrastructure.security.NorthernLightsPrincipal;
import io.northernlights.api.core.infrastructure.security.config.SecurityConfiguration;
import io.northernlights.commons.TimeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.config.*;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.resource.WebJarsResourceResolver;
import org.springframework.web.reactive.result.view.HttpMessageWriterView;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static java.util.TimeZone.getTimeZone;
import static java.util.TimeZone.setDefault;
import static org.springdoc.core.Constants.CLASSPATH_RESOURCE_LOCATION;
import static org.springdoc.core.Constants.DEFAULT_WEB_JARS_PREFIX_URL;
import static org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Import(SecurityConfiguration.class)
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
    public LocalConversationEventFlow localConversationEventFlow() {
        return new LocalConversationEventFlow();
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
    public void configureViewResolvers(ViewResolverRegistry registry) {
        Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();
        registry.defaultViews(new HttpMessageWriterView(encoder));
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
    public RouterFunction<ServerResponse> indexRouter(
        @Value("classpath:/static/index.html")
            Resource indexHtml
    ) {
        IndexPageRequestPredicate predicate = new IndexPageRequestPredicate();
        HandlerFunction<ServerResponse> indexHtmlResponse =
            request -> ok()
                .contentType(MediaType.TEXT_HTML)
                .bodyValue(indexHtml);
        return RouterFunctions.route(predicate, indexHtmlResponse);
    }

    @Bean
    public RouterFunction<ServerResponse> staticResourcesRouter() {
        return RouterFunctions.resources("/**", new ClassPathResource("static/"));
    }

    private static class IndexPageRequestPredicate implements RequestPredicate {
        private final AntPathMatcher antPathMatcher = new AntPathMatcher();

        @Override
        public boolean test(ServerRequest request) {
            String path = request.path();
            return request.method() == HttpMethod.GET
                && (path.equals("/") || (pathIsNotAFile(path) && pathIsNotApi(path)));
        }

        private boolean pathIsNotAFile(String path) {
            return antPathMatcher.match("/**/{path:[^\\.]*}", path);
        }

        private boolean pathIsNotApi(String path) {
            return !antPathMatcher.match("/v1/chat/api/**", path);
        }
    }

    @Bean
    public RouterFunction<ServerResponse> eventSourcePolyfillRouter(@Value("classpath:/static/eventsource.js") final Resource eventSourceJs) {
        return RouterFunctions.route(RequestPredicates.GET("/eventsource.js"),
            request -> ok().contentType(MediaType.valueOf("text/javascript")).bodyValue(eventSourceJs));
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
            .allowedMethods("*")
            .maxAge(3600);
    }
}
