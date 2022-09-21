package io.northernlights.commons;

import org.slf4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

public class ReactiveTimer {

    public static <T> Mono<T> time(Logger log, String label, Mono<T> flux) {
        return Mono.fromSupplier(System::nanoTime)
            .flatMap(time -> flux
                .doFirst(() -> log.info("{}...", label))
                .doFinally(sig -> log.info("{}: took {} ms", label, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - time))));
    }

    public static <T> Flux<T> time(Logger log, String label, Flux<T> flux) {
        return Mono.fromSupplier(System::nanoTime)
            .flatMapMany(time -> flux
                .doFirst(() -> log.info("{}...", label))
                .doFinally(sig -> log.info("{}: took {} ms", label, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - time))));
    }

}
