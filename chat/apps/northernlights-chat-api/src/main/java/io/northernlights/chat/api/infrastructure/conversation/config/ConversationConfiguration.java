package io.northernlights.chat.api.infrastructure.conversation.config;

import io.northernlights.api.core.error.infrastructure.ErrorHandler;
import io.northernlights.chat.api.application.conversation.ChatCommands;
import io.northernlights.chat.api.domain.conversation.ConversationEventPublisher;
import io.northernlights.chat.api.infrastructure.LocalConversationEventFlow;
import io.northernlights.chat.api.infrastructure.conversation.http.ChatApiAdapter;
import io.northernlights.chat.api.infrastructure.conversation.http.ChatHandler;
import io.northernlights.chat.store.chatter.ChatterStore;
import io.northernlights.chat.store.chatter.ChatterStoreConfigurationInMemory;
import io.northernlights.chat.store.conversation.ConversationStore;
import io.northernlights.chat.store.conversation.ConversationStoreConfigurationInMemory;
import io.northernlights.chat.store.ssekey.SseKeyStore;
import io.northernlights.chat.store.ssekey.SseKeyStoreConfigurationInMemory;
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
    ChatterStoreConfigurationInMemory.class, // TODO: switch to R2DBC implementation
    ConversationStoreConfigurationInMemory.class, // TODO: switch to R2DBC implementation
    SseKeyStoreConfigurationInMemory.class // TODO: switch to R2DBC implementation
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
                                     SseKeyStore sseKeyStore,
                                     ConversationEventPublisher conversationEventPublisher) {
        return new ChatCommands(timeService, chatterStore, conversationStore, sseKeyStore, conversationEventPublisher);
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
//        @RouterOperation(method = GET, path = CHAT_CONVERSATION + "/{" + CHATTER_ID + "}", beanClass = ChatHandler.class, beanMethod = "getChat"),// beanMethod = "getAll"
//        @RouterOperation(method = GET, path = CHAT_CONVERSATION + "/open", beanClass = ChatHandler.class, beanMethod = "countChat"),//,// beanMethod = "getById"
//        @RouterOperation(method = GET, path = CHAT_CONVERSATION, beanClass = ChatHandler.class, beanMethod = "listChat"),// beanMethod = "getById"
//        @RouterOperation(method = PUT, path = CHAT_CONVERSATION + "/{" + CHATTER_ID + "}", beanClass = ChatHandler.class, beanMethod = "updateChat"),// beanMethod = "save"
//        @RouterOperation(method = POST, path = CHAT_CONVERSATION, beanClass = ChatHandler.class, beanMethod = "updateChat") // beanMethod = "delete"
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
