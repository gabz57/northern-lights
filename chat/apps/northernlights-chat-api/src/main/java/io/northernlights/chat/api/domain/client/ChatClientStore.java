package io.northernlights.chat.api.domain.client;

import io.northernlights.chat.api.domain.client.model.ChatClientID;
import io.northernlights.chat.api.domain.client.model.ChatData;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.ssekey.SseChatKey;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChatClientStore {
    Flux<ChatData> loadPreviousData(ChatClientID chatClientId, SseChatKey sseChatKey);

    Mono<ChatData> loadConversationInstallData(ConversationId conversationId);

    Mono<List<ConversationId>> loadConversationIds(ChatClientID chatClientId);

    Mono<ChatterId> authenticate(SseChatKey sseChatKey);

    void revoke(SseChatKey sseChatKey);
}
