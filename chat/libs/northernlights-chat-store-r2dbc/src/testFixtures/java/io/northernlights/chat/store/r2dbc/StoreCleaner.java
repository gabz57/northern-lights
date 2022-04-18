package io.northernlights.chat.store.r2dbc;

import io.northernlights.chat.store.r2dbc.chatter.ChatterConversationRepository;
import io.northernlights.chat.store.r2dbc.chatter.ChattersRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationDataReadMarkerRepository;
import io.northernlights.chat.store.r2dbc.conversation.ConversationDataRepository;
import io.northernlights.chat.store.r2dbc.ssekey.ChatterSseChatKeyRepository;
import lombok.RequiredArgsConstructor;
import reactor.test.StepVerifier;

@RequiredArgsConstructor
public class StoreCleaner {
    private final ChattersRepository chattersRepository;
    private final ChatterConversationRepository chatterConversationRepository;
    private final ChatterSseChatKeyRepository chatterSseChatKeyRepository;
    private final ConversationDataReadMarkerRepository conversationDataReadMarkerRepository;
    private final ConversationDataRepository conversationDataRepository;

    public void clearDatabase() {
        chatterSseChatKeyRepository.deleteAll()
            .thenMany(conversationDataReadMarkerRepository.deleteAll())
            .thenMany(conversationDataRepository.deleteAll())
            .thenMany(chatterConversationRepository.deleteAll())
            .thenMany(chattersRepository.deleteAll())
            .as(StepVerifier::create)
            .verifyComplete();
    }
}
