package io.northernlights.chat.api.infrastructure.user.config;

import io.northernlights.api.core.error.infrastructure.ErrorHandler;
import io.northernlights.chat.domain.application.user.UserCommands;
import io.northernlights.chat.api.infrastructure.user.http.UserApiAdapter;
import io.northernlights.chat.api.infrastructure.user.http.UserHandler;
import io.northernlights.chat.domain.store.chatter.ChatterStore;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.chat.domain.store.user.UserStore;
import io.northernlights.commons.TimeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.northernlights.chat.domain.ApiConstants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserConfiguration {

    @Bean
    public UserCommands userCommands(TimeService timeService, UserStore userStore,
                                     ChatterStore chatterStore, ConversationStore conversationStore) {
        return new UserCommands(timeService, userStore, chatterStore, conversationStore);
    }

    @Bean
    public UserApiAdapter userApiAdapter() {
        return new UserApiAdapter();
    }

    @Bean
    public UserHandler userHandler(UserCommands userCommands, UserApiAdapter userApiAdapter) {
        return new UserHandler(userCommands, userApiAdapter);
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        return route(GET(USER_API_INFO).and(accept(APPLICATION_JSON)), userHandler::userInfo)
            .andRoute(POST(USER_API_SUBSCRIBE).and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), userHandler::subscribeUser)
            .filter(new ErrorHandler());
    }
}
