package io.northernlights.chat.api.domain.client;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
public class ChatClient {

    private final ChatClientID chatClientId;
    private final ChatterEventProvider chatterEventProvider;
    private final ChatterDataStore chatterDataStore;
    private final Sinks.Many<ChatData> chatterDataSink = Sinks.many().unicast().onBackpressureBuffer();
    private Disposable disposableChatDataFlow;

    public ChatClient(ChatClientID chatClientId, ChatterEventProvider chatterEventProvider, ChatterDataStore chatterDataStore) {
        this.chatClientId = chatClientId;
        this.chatterEventProvider = chatterEventProvider;
        this.chatterDataStore = chatterDataStore;
    }

    public Flux<ChatData> connect(String lastChatterEventId) {
        log.info("ChatClient::subscribeChatterFlowSince " + lastChatterEventId + " for clientID: " + this.chatClientId);
        return Flux.concat(
            previousData(lastChatterEventId),
            liveData()
        );
    }

    public void stop() {
        log.info("ChatClient::stop clientID: " + this.chatClientId);
        disposableChatDataFlow.dispose();
    }

    private Flux<ChatData> previousData(String lastChatterEventId) {
        return loadPreviousData(lastChatterEventId)
            .doOnSubscribe(s -> log.info("Loading all user previous data ..."))
            .doOnError(e -> log.error("Loading all user previous data FAILED", e))
            .doOnComplete(() -> {
                log.info("Loading all user previous data DONE");
                startSendingLiveEvent();
            });
    }

    private Flux<ChatData> loadPreviousData(String lastChatterEventId) {
        return chatterDataStore.loadPreviousData(this.chatClientId, lastChatterEventId);
    }

    private Flux<ChatData> liveData() {
        return chatterDataSink.asFlux()
            .doOnSubscribe(s -> log.info("Subscribed to chatDataSink ..."))
            .doOnError(e -> log.error("Error in chatDataSink", e))
            .doOnComplete(() -> log.info("chatDataSink terminated"));
    }

    private void startSendingLiveEvent() {
        log.info("ChatClient::startSendingLiveEvent for clientID: " + this.chatClientId);
        disposableChatDataFlow = Flux.from(chatterEventProvider.chatterFlow(this.chatClientId))
            .doOnSubscribe(s -> log.info("subscribed to chatterEventProvider.chatterFlow for " + this.chatClientId))
            .subscribe(chatterDataSink::tryEmitNext);
    }

}
