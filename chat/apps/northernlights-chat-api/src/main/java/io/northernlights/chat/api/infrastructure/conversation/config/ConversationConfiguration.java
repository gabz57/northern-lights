package io.northernlights.chat.api.infrastructure.conversation.config;

import io.northernlights.api.core.infrastructure.error.ErrorHandler;
import io.northernlights.chat.api.application.conversation.ChatCommands;
import io.northernlights.chat.api.domain.conversation.ConversationEventPublisher;
import io.northernlights.chat.api.infrastructure.LocalConversationEventFlow;
import io.northernlights.chat.api.infrastructure.conversation.http.ChatApiAdapter;
import io.northernlights.chat.api.infrastructure.conversation.http.ChatHandler;
import io.northernlights.chat.store.chatter.domain.ChatterStore;
import io.northernlights.chat.store.chatter.infrastructure.ChatterStoreConfiguration;
import io.northernlights.chat.store.conversation.domain.ConversationStore;
import io.northernlights.chat.store.conversation.infrastructure.ConversationStoreConfiguration;
import io.northernlights.commons.TimeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Import({
    ChatterStoreConfiguration.class,
    ConversationStoreConfiguration.class
})
@Configuration
public class ConversationConfiguration {

    public static final String CHAT_CONVERSATION = "/v1/chat/api";

//    @Qualifier("chatter")
//    @Bean
//    public StoreProperties chatterStoreProperties(ChatApiProperties chatApiProperties) {
//        return ofNullable(chatApiProperties)
//            .map(ChatApiProperties::getStore)
//            .map(ChatApiStoreProperties::getChatter)
////            .filter(storeProperties -> storeProperties.getR2dbc() != null && storeProperties.getR2dbc().getUrl() != null)
//            .orElseGet(() -> ofNullable(chatApiProperties).map(ChatApiProperties::getStore).orElseThrow());
//    }
//
//    @Qualifier("conversation")
//    @Bean
//    public StoreProperties conversationStoreProperties(ChatApiProperties chatApiProperties) {
//        return ofNullable(chatApiProperties)
//            .map(ChatApiProperties::getStore)
//            .map(ChatApiStoreProperties::getConversation)
////            .filter(storeProperties -> storeProperties.getR2dbc() != null && storeProperties.getR2dbc().getUrl() != null)
//            .orElseGet(() -> ofNullable(chatApiProperties).map(ChatApiProperties::getStore).orElseThrow());
//    }

    @Bean
    public ConversationEventPublisher conversationEventPublisher(LocalConversationEventFlow localConversationEventFlow) {
        return e -> {
            localConversationEventFlow.publish(e);
            return Mono.empty();
        };
    }

    @Bean
    public ChatCommands chatCommands(TimeService timeService,
                                     ChatterStore chatterStore,
                                     ConversationStore conversationStore,
                                     ConversationEventPublisher conversationEventPublisher) {
        return new ChatCommands(timeService, chatterStore, conversationStore, conversationEventPublisher);
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
//        @RouterOperation(method = GET, path = CUSTOM_PRODUCTS + "/{" + PRODUCT_ID + "}", beanClass = ChatHandler.class, beanMethod = "getChat"),// beanMethod = "getAll"
//        @RouterOperation(method = GET, path = CUSTOM_PRODUCTS + "/count", beanClass = ChatHandler.class, beanMethod = "countChat"),//,// beanMethod = "getById"
//        @RouterOperation(method = GET, path = CUSTOM_PRODUCTS, beanClass = ChatHandler.class, beanMethod = "listChat"),// beanMethod = "getById"
//        @RouterOperation(method = PUT, path = CUSTOM_PRODUCTS + "/{" + PRODUCT_ID + "}", beanClass = ChatHandler.class, beanMethod = "updateChat"),// beanMethod = "save"
//        @RouterOperation(method = POST, path = CUSTOM_PRODUCTS, beanClass = ChatHandler.class, beanMethod = "updateChat") // beanMethod = "delete"
//    })
    @Bean
    public RouterFunction<ServerResponse> chatRoutes(ChatHandler chatHandler) {
        return route(POST(CHAT_CONVERSATION + "/auth").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::authenticateChatter)
            .andRoute(POST(CHAT_CONVERSATION + "/open").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::createConversation)
            .andRoute(POST(CHAT_CONVERSATION + "/mark-as-read").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::markAsRead)
            .andRoute(POST(CHAT_CONVERSATION + "/message").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::sendMessage)
            .andRoute(POST(CHAT_CONVERSATION + "/invite-chatter").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::inviteChatter)
            .filter(new ErrorHandler());
    }
}
