package io.northernlights.chat.api.infrastructure.conversation.config;

import io.northernlights.api.core.infrastructure.error.ErrorHandler;
import io.northernlights.chat.api.application.conversation.ChatCommands;
import io.northernlights.chat.api.infrastructure.ChatApiProperties;
import io.northernlights.chat.api.infrastructure.ChatApiStoreProperties;
import io.northernlights.chat.api.infrastructure.conversation.http.ChatHandler;
import io.northernlights.chat.domain.ConversationEventPublisher;
import io.northernlights.chat.store.chatter.domain.ChatterStore;
import io.northernlights.chat.store.chatter.infrastructure.ChatterStoreConfiguration;
import io.northernlights.chat.store.conversation.domain.ConversationStore;
import io.northernlights.chat.store.conversation.infrastructure.ConversationStoreConfiguration;
import io.northernlights.chat.store.r2dbc.StoreProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Import({
    ChatterStoreConfiguration.class,
    ConversationStoreConfiguration.class
})
@Configuration
public class ChatConfiguration {

    public static final String CHAT_CONVERSATION = "/v1/chat/api"; // {" + /*PARTNER_ID +*/ "}";

    @Qualifier("chat")
    @Bean
    public StoreProperties storeProperties(ChatApiProperties productApiProperties) {
        return ofNullable(productApiProperties)
            .map(ChatApiProperties::getStore)
            .map(ChatApiStoreProperties::getChat)
            .filter(storeProperties -> storeProperties.getR2dbc() != null && storeProperties.getR2dbc().getUrl() != null)
            .orElseGet(() -> ofNullable(productApiProperties).map(ChatApiProperties::getStore).orElseThrow());
    }

    @Bean
    public ChatCommands chatCommands(ChatterStore chatterStore,
                                     ConversationStore conversationStore,
                                     ConversationEventPublisher conversationEventPublisher) {
        return new ChatCommands(chatterStore, conversationStore, conversationEventPublisher);
    }

    //
//    @Bean
//    public ChatService customProductsService(ChatStore chatStore) {
//        return new ChatServiceImpl(chatStore);
//    }
//
//    @Bean
//    public ChatQueryFactory customProductQueryFactory(ChatService customProductsService) {
//        return new ChatQueryFactory(customProductsService);
//    }
//
//    @Bean
//    public ChatCommandFactory customProductCommandFactory(ChatService customProductsService) {
//        return new ChatCommandFactory(customProductsService);
//    }
//
    @Bean
    public ChatHandler chatHandler(ChatCommands chatCommands) {
        return new ChatHandler(chatCommands);
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
        return RouterFunctions
            .route(RequestPredicates.POST(CHAT_CONVERSATION + "/open").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::createConversation)
            .andRoute(RequestPredicates.POST(CHAT_CONVERSATION + "/mark-as-read").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::markAsRead)
            .andRoute(RequestPredicates.POST(CHAT_CONVERSATION + "/message").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), chatHandler::sendMessage)
            .filter(new ErrorHandler());
    }
}
