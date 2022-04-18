package io.northernlights.chat.api.infrastructure;

import io.northernlights.commons.TimeConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.result.view.HttpMessageWriterView;

import static io.northernlights.chat.domain.ApiConstants.CHAT_API;
import static io.northernlights.chat.domain.ApiConstants.USER_API;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Import({TimeConfiguration.class, SecurityConfiguration.class})
//@Import({MdcContextLifterConfiguration.class, TimeConfiguration.class, SecurityConfiguration.class})
@Configuration
@EnableWebFlux
public class ChatApiHttpConfiguration implements WebFluxConfigurer {

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
    public RouterFunction<ServerResponse> indexRouter(@Value("classpath:/static/index.html") Resource indexHtml) {
        return RouterFunctions.route(
            new IndexPageRequestPredicate(),
            request -> ok()
                .contentType(MediaType.TEXT_HTML)
                .bodyValue(indexHtml)
        );
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
            return !antPathMatcher.match(CHAT_API + "/**", path) && !antPathMatcher.match(USER_API + "/**", path);
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
            .allowedMethods("*")
            .maxAge(3600);
    }
}
