package io.northernlights.chat.api.domain.client.model;

import io.northernlights.chat.api.domain.client.ChatClientStore;
import io.northernlights.chat.api.domain.client.ChatDataProvider;
import io.northernlights.chat.api.domain.client.model.ChatDataUpdate.ChatterAddValue;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import io.northernlights.chat.domain.model.ssekey.SseChatKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;

@Slf4j
public class ChatClient {

    private final ChatClientID chatClientId;
    private final ChatDataProvider chatDataProvider;
    private final ChatClientStore chatClientStore;
    private final Sinks.Many<ChatData> chatterDataSink = Sinks.many().unicast().onBackpressureBuffer();
    private Disposable disposableChatDataFlow;
    private List<ConversationId> conversationIds;

    public ChatClient(ChatClientID chatClientId, ChatDataProvider chatDataProvider, ChatClientStore chatClientStore) {
        this.chatClientId = chatClientId;
        this.chatDataProvider = chatDataProvider;
        this.chatClientStore = chatClientStore;
    }

    @Transactional(readOnly = true)
    public Flux<ChatData> connect(SseChatKey sseChatKey) {
        log.info("ChatClient::connect using sseChatKey " + sseChatKey + " for clientID: " + this.chatClientId);
        return chatClientStore.loadConversationIds(this.chatClientId)
            .doOnNext(ids -> this.conversationIds = ids)
            .thenMany(Flux.concat(
                hello(sseChatKey.getId().toString()),
                previousData(sseChatKey).doOnComplete(() -> log.info("Connect: Previous Data Completed")),
                liveData().doOnComplete(() -> log.info("Connect: Live Data Completed"))
            )).doOnComplete(() -> log.info("Connect: COMPLETED..."));
    }

    private Flux<ChatData> hello(String sseChatKey) {
        return Flux.just(ChatDataHello.builder()
            .sseChatKey(sseChatKey)
            .build());
    }

    public void stop() {
        log.info("ChatClient::stop clientID: " + this.chatClientId);
        disposableChatDataFlow.dispose();
    }

    private Flux<ChatData> previousData(SseChatKey sseChatKey) {
        return chatClientStore.loadPreviousData(this.chatClientId, sseChatKey)
            .doOnSubscribe(s -> log.info("Loading all user previous data ..."))
            .doOnError(e -> log.error("Loading all user previous data FAILED", e))
            .doOnComplete(() -> {
                log.info("Loading all user previous data DONE");
                startSendingLiveEvent();
            });
    }

    private Flux<ChatData> liveData() {
        return chatterDataSink.asFlux()
            .doOnSubscribe(s -> log.info("ChatterDataSink subscribed"))
            .doOnNext(n -> log.info("LIVE :> {}", n.getChatDataType()))
            .doOnError(e -> log.error("ChatterDataSink error", e))
            .doOnComplete(() -> log.info("ChatterDataSink terminated"));
    }

    private void startSendingLiveEvent() {
        log.info("ChatClient::startSendingLiveEvent for clientID: " + this.chatClientId);
        disposableChatDataFlow = chatDataProvider.chatterFlow(this.chatClientId, this.conversationIds)
            .doOnSubscribe(s -> log.info("Subscribed to chatterEventProvider.chatterFlow for " + this.chatClientId))
            .doOnTerminate(() -> log.warn("Subscription to chatterEventProvider.chatterFlow for " + this.chatClientId + " TERMINATED"))
            .subscribe(t -> {
                // OMG, called on each data...
                //
                // When someone is invited to a conversation, the invited one may not know all other chatters
                // we reuse the method and load the whole conversation instead of updating the invited chatter
                if (t.getChatDataType() == ChatData.ChatDataType.LIVE_UPDATE) {
                    ChatDataUpdate chatDataUpdate = (ChatDataUpdate) t;
                    ChatterAddValue chatterAdd = chatDataUpdate.getChatterAdd();
                    // only invitee is interested, intercept and replace what he receives with previous content
                    if (chatterAdd != null && chatterAdd.getChatterId().equals(this.chatClientId.getChatterId())) {
                        chatClientStore.installConversation(chatDataUpdate.getConversationId())
                            .doOnNext(chatterDataSink::tryEmitNext)
                            .subscribe();
                        return;
                    }
                }
                chatterDataSink.tryEmitNext(t);
            });
    }

}
