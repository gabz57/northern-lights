package io.northernlights.chat.domain.application.conversation;

import io.northernlights.chat.domain.application.UseCase;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.conversation.data.ConversationDataId;
import io.northernlights.chat.domain.model.ssekey.SseChatKey;
import io.northernlights.chat.domain.store.ssekey.SseKeyStore;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Mono;

import java.util.Map;

import static io.northernlights.chat.domain.application.conversation.InitializeSseChatCommand.InitializeSseChatCommandInput;
import static io.northernlights.chat.domain.application.conversation.InitializeSseChatCommand.InitializeSseChatCommandResult;

@RequiredArgsConstructor
public class InitializeSseChatCommand implements UseCase<InitializeSseChatCommandInput, InitializeSseChatCommandResult> {

    private final SseKeyStore sseKeyStore;

    public Mono<InitializeSseChatCommandResult> execute(InitializeSseChatCommandInput input) {
        return sseKeyStore.storeStatusAndGenerateSseChatKey(input.getChatterId(), input.getConversationStatus())
            .map(InitializeSseChatCommandResult::new);
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class InitializeSseChatCommandInput {
        ChatterId chatterId;
        Map<ConversationId, ConversationDataId> conversationStatus;
    }

    @Value
    @Builder
    @RequiredArgsConstructor
    public static class InitializeSseChatCommandResult {
        SseChatKey sseChatKey;
    }
}
