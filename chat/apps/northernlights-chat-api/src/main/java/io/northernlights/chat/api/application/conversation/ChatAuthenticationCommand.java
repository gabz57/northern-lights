package io.northernlights.chat.api.application.conversation;

import io.northernlights.chat.api.application.UseCase;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.ssekey.SseChatKey;
import io.northernlights.chat.store.ssekey.SseKeyStore;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import java.util.Map;

import static io.northernlights.chat.api.application.conversation.ChatAuthenticationCommand.ChatAuthenticationCommandInput;
import static io.northernlights.chat.api.application.conversation.ChatAuthenticationCommand.ChatAuthenticationCommandResult;

@RequiredArgsConstructor
public class ChatAuthenticationCommand implements UseCase<ChatAuthenticationCommandInput, ChatAuthenticationCommandResult> {

    private final SseKeyStore sseKeyStore;

    public Mono<ChatAuthenticationCommandResult> execute(ChatAuthenticationCommandInput input) {
        return sseKeyStore.storeStatusAndGenerateSseChatKey(input.getChatterId(), input.getConversationStatus())
            .map(ChatAuthenticationCommandResult::new);
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class ChatAuthenticationCommandInput {
        ChatterId chatterId;
        Map<ConversationId, ConversationDataId> conversationStatus;
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class ChatAuthenticationCommandResult {
        SseChatKey sseChatKey;
    }
}
