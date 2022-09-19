package io.northernlights.chat.store.r2dbc.chatter;

import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.store.chatter.ChatterStore;
import io.northernlights.chat.store.r2dbc.chatter.model.ChatterModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class R2dbcChatterStore implements ChatterStore {

    private final ChattersRepository chattersRepository;

    @Transactional(readOnly = true)
    public Mono<List<Chatter>> listChatters() {
        return chattersRepository.findAll()
            .map(ChatterModel::toChatter)
            .collectList();
    }
    @Transactional(readOnly = true)
    public Mono<List<Chatter>> listChatters(List<ChatterId> chatterIds) {
        return chattersRepository.findAllById(chatterIds.stream().map(ChatterId::getId).collect(toList()))
            .map(ChatterModel::toChatter)
            .collectList();
    }

    @Transactional
    public Mono<Chatter> insertChatter(Chatter chatter) {
        return chattersRepository.save(ChatterModel.of(chatter, true))
            .map(ChatterModel::toChatter);
    }

}
