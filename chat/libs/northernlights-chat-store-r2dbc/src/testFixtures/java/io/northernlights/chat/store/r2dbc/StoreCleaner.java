package io.northernlights.chat.store.r2dbc;

import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.store.r2dbc.chatter.ChatterConversationRepository;
import io.northernlights.chat.store.r2dbc.chatter.ChattersRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationDataReadMarkerRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationDataRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationsRepository;
import io.northernlights.chat.store.r2dbc.conversation.model.ConversationModel;
import io.northernlights.chat.store.r2dbc.event.ChatEventRepository;
import io.northernlights.chat.store.r2dbc.ssekey.ChatterSseChatKeyRepository;
import lombok.RequiredArgsConstructor;
import reactor.test.StepVerifier;

@RequiredArgsConstructor
public class StoreCleaner {
    private final ChattersRepository chattersRepository;
    private final ConversationsRepository conversationsRepository;
    private final ChatterConversationRepository chatterConversationRepository;
    private final ChatterSseChatKeyRepository chatterSseChatKeyRepository;
    private final ConversationDataReadMarkerRepository conversationDataReadMarkerRepository;
    private final ConversationDataRepository conversationDataRepository;
    private final ChatEventRepository chatEventRepository;

    public void clearDatabase() {
        chatEventRepository.deleteAll()
            .thenMany(conversationsRepository.findAll())
            .mapNotNull(ConversationModel::getId)
            .map(ConversationId::new)
            .flatMap(conversationId ->
                conversationDataReadMarkerRepository.deleteAll(conversationId)
                    .then(conversationDataRepository.deleteAll(conversationId)))
            .then(conversationsRepository.deleteAll())
            .thenMany(chatterSseChatKeyRepository.deleteAll())
            .thenMany(chatterConversationRepository.deleteAll())
            .thenMany(chattersRepository.deleteAll())
            .as(StepVerifier::create)
            .verifyComplete();
    }
}
