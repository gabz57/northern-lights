package io.northernlights.chat.domain.store.chatter;

import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ChatterStore {

    Mono<List<Chatter>> listChatters();
    Mono<List<Chatter>> listChatters(List<ChatterId> chatterIds);

    Mono<Chatter> insertChatter(Chatter chatter);
}
