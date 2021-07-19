package io.northernlights.chat.api.infrastructure;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {
    public static final String BEARER_KEY = "northernlights-JWT-token";
    public static final String OAUTH_2_KEY = "oauth2";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(getInfo())
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Localhost"),
                new Server().url("https://api.northernlights.io").description("Production"),
                new Server().url("https://sandbox.api.northernlights.io").description("Sandbox")
            ))
            .components(new Components()
                .addSecuritySchemes(OAUTH_2_KEY, new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .flows(new OAuthFlows().clientCredentials(new OAuthFlow()
                        .tokenUrl("https://northernlights-chat.eu.auth0.com/oauth/token")
                        .refreshUrl("")
                        .scopes(new Scopes()
//                            .addString(PRODUCTS_READ_COOPERATIVE.scope, PRODUCTS_READ_COOPERATIVE.description)
//                            .addString(PRODUCTS_MANAGE_COOPERATIVE.scope, PRODUCTS_MANAGE_COOPERATIVE.description)
                        )
                    ))
                    .description("Authentication via OAuth2 Client Credential Flow")
                )
                .addSecuritySchemes(BEARER_KEY, new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT bearer token")
                )
            )
            // This adds the given token to each query instead of using security = @SecurityRequirement(name = BEARER_KEY) in @Operation
            .security(List.of(
                new SecurityRequirement().addList(BEARER_KEY)
            ));
    }

    @Bean
    public GroupedOpenApi chatOpenApi() {
        return GroupedOpenApi.builder()
            .group("chat")
            .pathsToMatch("/v1/chat/*/sse/**", "/v1/chat/*/mgmt/**")
            .addOpenApiCustomiser(errorOpenApiCustomiser())
            .build();
    }

//    @Bean
//    public GroupedOpenApi chatMgmtOpenApi() {
//        return GroupedOpenApi.builder()
//            .group("catalog-mgmt")
//            .pathsToMatch("/v1/chat/*/mgmt/**")
//            .addOpenApiCustomiser(errorOpenApiCustomiser())
//            .build();
//    }

    private Info getInfo() {
        return new Info()
            .title("Northern Lights API")
            .version("1.0.0")
            .description("Open API by northernlights")
            .contact(new Contact()
                .name("Support northernlights.io")
                .email("developers@northernlights.io"));
    }

    private OpenApiCustomiser errorOpenApiCustomiser() {
        return openApi -> openApi.getPaths().values()
            .forEach(pathItem -> pathItem.readOperationsMap().values()
                .stream().map(Operation::getResponses)
                .forEach(responses -> {
                    responses.addApiResponse("400", new ApiResponse().description("Bad request"));
                    responses.addApiResponse("401", new ApiResponse().description("Unauthorized"));
                    responses.addApiResponse("403", new ApiResponse().description("Forbidden"));
                    responses.addApiResponse("404", new ApiResponse().description("Resource not found"));
                    responses.addApiResponse("500", new ApiResponse().description("Internal server error"));
                    responses.addApiResponse("502", new ApiResponse().description("Bad gateway"));
                    responses.addApiResponse("503", new ApiResponse().description("Service unavailable"));
                })
            );
    }

}
