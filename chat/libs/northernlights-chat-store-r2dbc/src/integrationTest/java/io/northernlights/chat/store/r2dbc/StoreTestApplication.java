package io.northernlights.chat.store.r2dbc;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * This class provides an application context for R2DBC Integration Tests
 */
@SpringBootApplication(
    exclude = {WebMvcAutoConfiguration.class, ErrorMvcAutoConfiguration.class, SecurityAutoConfiguration.class},
    scanBasePackages = "io.northernlights.chat.store.r2dbc"
)
public class StoreTestApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(StoreTestApplication.class)
            .web(WebApplicationType.REACTIVE)
            .run(args);
    }
}
