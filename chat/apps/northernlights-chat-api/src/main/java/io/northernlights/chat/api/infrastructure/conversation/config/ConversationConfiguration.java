package io.northernlights.chat.api.infrastructure.conversation.config;

import io.northernlights.api.core.error.infrastructure.ErrorHandler;
import io.northernlights.chat.domain.application.conversation.ChatCommands;
import io.northernlights.chat.api.infrastructure.conversation.http.ChatApiAdapter;
import io.northernlights.chat.api.infrastructure.conversation.http.ChatHandler;
import io.northernlights.chat.domain.store.conversation.ConversationStore;
import io.northernlights.chat.domain.store.ssekey.SseKeyStore;
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
public class ConversationConfiguration {

    @Bean
    public ChatCommands chatCommands(TimeService timeService, ConversationStore conversationStore, SseKeyStore sseKeyStore) {
        return new ChatCommands(timeService,  conversationStore, sseKeyStore);
    }

    @Bean
    public ChatApiAdapter chatApiAdapter() {
        return new ChatApiAdapter();
    }

    @Bean
    public ChatHandler chatHandler(ChatCommands chatCommands, ChatApiAdapter chatApiAdapter) {
        return new ChatHandler(chatCommands, chatApiAdapter);
    }

    //
//    @RouterOperations({
//        @RouterOperation(method = GET, path = CHAT_API + "/{" + CHATTER_ID + "}", beanClass = ChatHandler.class, beanMethod = "getChat"),// beanMethod = "getAll"
//        @RouterOperation(method = GET, path = CHAT_API + "/open", beanClass = ChatHandler.class, beanMethod = "countChat"),//,// beanMethod = "getById"
//        @RouterOperation(method = GET, path = CHAT_API, beanClass = ChatHandler.class, beanMethod = "listChat"),// beanMethod = "getById"
//        @RouterOperation(method = PUT, path = CHAT_API + "/{" + CHATTER_ID + "}", beanClass = ChatHandler.class, beanMethod = "updateChat"),// beanMethod = "save"
//        @RouterOperation(method = POST, path = CHAT_API, beanClass = ChatHandler.class, beanMethod = "updateChat") // beanMethod = "delete"
//    })
    @Bean
    public RouterFunction<ServerResponse> chatRoutes(ChatHandler chatHandler) {
        return route(POST(CHAT_API_INITIALIZE_SSE).and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::initializeSseChat)
            .andRoute(POST(CHAT_API_OPEN).and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::createConversation)
            .andRoute(POST(CHAT_API_MARK_AS_READ).and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::markAsRead)
            .andRoute(POST(CHAT_API_MESSAGE).and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::sendMessage)
            .andRoute(POST(CHAT_API_INVITE_CHATTER).and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::inviteChatter)
            .filter(new ErrorHandler());
    }
}
