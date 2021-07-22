package io.northernlights.chat.api.domain.client;


import io.northernlights.chat.api.domain.client.model.ChatClientID;
import io.northernlights.chat.api.domain.client.model.ChatData;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatDataProvider {
    Flux<ChatData> chatterFlow(ChatClientID chatClientId, List<ConversationId> conversationIds);
}
