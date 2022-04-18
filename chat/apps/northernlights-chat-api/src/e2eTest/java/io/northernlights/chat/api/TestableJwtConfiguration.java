package io.northernlights.chat.api;

import io.northernlights.security.jwt.JwtProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestableJwtConfiguration {

    @Primary
    @Bean
    public JwtProvider jwtProvider() {
        return new TestJwtProvider();
    }
}
