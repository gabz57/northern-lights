package io.northernlights.chat.api.domain.client.model;

import io.northernlights.chat.api.domain.client.ChatClientStore;
import io.northernlights.chat.api.domain.client.ChatDataProvider;
import io.northernlights.chat.domain.model.conversation.ConversationId;
import lombok.extern.slf4j.Slf4j;
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

    public Flux<ChatData> connect(String sseChatKey) {
        log.info("ChatClient::connect using sseChatKey " + sseChatKey + " for clientID: " + this.chatClientId);
        return chatClientStore.loadConversationIds(this.chatClientId)
            .doOnNext(ids -> this.conversationIds = ids)
            .thenMany(Flux.concat(
                hello(sseChatKey),
                previousData(sseChatKey),
                liveData()
            ));
    }

    public void stop() {
        log.info("ChatClient::stop clientID: " + this.chatClientId);
        disposableChatDataFlow.dispose();
    }

    private Flux<ChatData> hello(String sseChatKey) {
        return Flux.just(ChatDataHello.builder()
            .sseChatKey(sseChatKey)
            .build());
    }

    private Flux<ChatData> previousData(String sseChatKey) {
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
            .doOnSubscribe(s -> log.info("Subscribed to chatDataSink ..."))
            .doOnError(e -> log.error("Error in chatDataSink", e))
            .doOnComplete(() -> log.info("chatDataSink terminated"));
    }

    private void startSendingLiveEvent() {
        log.info("ChatClient::startSendingLiveEvent for clientID: " + this.chatClientId);
        disposableChatDataFlow = chatDataProvider.chatterFlow(this.chatClientId, this.conversationIds)
            .doOnSubscribe(s -> log.info("Subscribed to chatterEventProvider.chatterFlow for " + this.chatClientId))
            .subscribe(chatterDataSink::tryEmitNext);
    }

}
