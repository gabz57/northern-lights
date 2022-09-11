package io.northernlights.chat.domain.application;

import reactor.core.publisher.Mono;

public interface UseCase<I, O> {

    Mono<O> execute(I request);
}
