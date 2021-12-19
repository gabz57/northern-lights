package io.northernlights.commons;

import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;
import reactor.util.annotation.NonNull;
import reactor.util.context.Context;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.stream.Collectors;

/**
 * Usage:
 * <pre>
 * return aMono.contextWrite(ctx -> ctx.put("correlationId", correlationId));
 * </pre>
 */
@Configuration
public class MdcContextLifterConfiguration {

    private static final String MDC_CONTEXT_REACTOR_KEY = MdcContextLifterConfiguration.class.getName();

    @PostConstruct
    public void contextOperatorHook() {
        Hooks.onEachOperator(MDC_CONTEXT_REACTOR_KEY, Operators.lift((ignored, subscriber) -> new MdcContextLifter<>(subscriber)));
    }

    @PreDestroy
    public void cleanupHook() {
        Hooks.resetOnEachOperator(MDC_CONTEXT_REACTOR_KEY);
    }

    public static class MdcContextLifter<T> implements CoreSubscriber<T> {
        private final CoreSubscriber<T> coreSubscriber;

        public MdcContextLifter(CoreSubscriber<T> coreSubscriber) {
            this.coreSubscriber = coreSubscriber;
        }

        @Override
        public void onSubscribe(@NonNull Subscription subscription) {
            coreSubscriber.onSubscribe(subscription);
        }

        @Override
        public void onNext(T t) {
            Context context = coreSubscriber.currentContext();
            if (!context.isEmpty()) {
                MDC.setContextMap(context.stream()
                    .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString())));
            } else {
                MDC.clear();
            }
            coreSubscriber.onNext(t);
        }

        @Override
        public void onError(Throwable throwable) {
            coreSubscriber.onError(throwable);
        }

        @Override
        public void onComplete() {
            coreSubscriber.onComplete();
        }
    }

}
