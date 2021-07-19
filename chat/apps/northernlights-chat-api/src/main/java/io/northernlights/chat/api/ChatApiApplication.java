package io.northernlights.chat.api;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(
        exclude = {WebMvcAutoConfiguration.class, ErrorMvcAutoConfiguration.class, SecurityAutoConfiguration.class},
        scanBasePackageClasses = ChatApiApplication.class
)
public class ChatApiApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ChatApiApplication.class)
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }
}
