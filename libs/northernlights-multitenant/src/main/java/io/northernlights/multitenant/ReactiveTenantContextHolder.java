package io.northernlights.multitenant;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.function.Function;

public final class ReactiveTenantContextHolder {

    private static final Class<?> TENANT_CONTEXT_KEY = TenantContext.class;

    private ReactiveTenantContextHolder() {
    }

    /**
     * Gets the {@code Mono<TenantContext>} from Reactor {@link ContextView}
     *
     * @return the {@code Mono<TenantContext>}
     */
    public static Mono<TenantContext> getContext() {
        return Mono.deferContextual(Mono::just)
            .filter(ReactiveTenantContextHolder::hasTenantContext)
            .flatMap(ReactiveTenantContextHolder::getTenantContext);
    }

    private static boolean hasTenantContext(ContextView context) {
        return context.hasKey(TENANT_CONTEXT_KEY);
    }

    private static Mono<TenantContext> getTenantContext(ContextView context) {
        return context.<Mono<TenantContext>>get(TENANT_CONTEXT_KEY);
    }

    /**
     * Clears the {@code Mono<TenantContext>} from Reactor {@link Context}
     *
     * @return Return a {@code Mono<Void>} which only replays complete and error signals
     * from clearing the context.
     */
    public static Function<Context, Context> clearContext() {
        return (context) -> context.delete(TENANT_CONTEXT_KEY);
    }

    /**
     * Creates a Reactor {@link Context} that contains the {@code Mono<TenantContext>}
     * that can be merged into another {@link Context}
     *
     * @param tenantContext the {@code Mono<TenantContext>} to set in the returned
     *                      Reactor {@link Context}
     * @return a Reactor {@link Context} that contains the {@code Mono<TenantContext>}
     */
    public static Context withTenantContext(Mono<? extends TenantContext> tenantContext) {
        return Context.of(TENANT_CONTEXT_KEY, tenantContext);
    }

    /**
     * A shortcut for {@link #withTenantContext(Mono)}
     *
     * @param tenant the {@link Tenant} to be used
     * @return a Reactor {@link Context} that contains the {@code Mono<TenantContext>}
     */
    public static Context withTenant(Tenant tenant) {
        return withTenantContext(Mono.just(new TenantContextImpl(tenant)));
    }

}
