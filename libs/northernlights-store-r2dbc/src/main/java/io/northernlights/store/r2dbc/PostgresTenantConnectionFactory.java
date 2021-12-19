//package io.northernlights.store.r2dbc;
//
//import io.northernlights.multitenant.ReactiveTenantContextHolder;
//import io.northernlights.multitenant.Tenant;
//import io.northernlights.multitenant.TenantContext;
//import io.r2dbc.spi.ConnectionFactory;
//import io.r2dbc.spi.ConnectionFactoryMetadata;
//import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
//import reactor.core.publisher.Mono;
//
//import java.util.Map;
//
//public class PostgresTenantConnectionFactory extends AbstractRoutingConnectionFactory {
//    public PostgresTenantConnectionFactory(Map<String, ConnectionFactory> tenants) {
//        setLenientFallback(false);
//        setTargetConnectionFactories(tenants);
//    }
//
//    static final class PostgresqlConnectionFactoryMetadata implements ConnectionFactoryMetadata {
//
//        static final PostgresqlConnectionFactoryMetadata INSTANCE = new PostgresqlConnectionFactoryMetadata();
//
//        public static final String NAME = "PostgreSQL";
//
//        private PostgresqlConnectionFactoryMetadata() {
//        }
//
//        @Override
//        public String getName() {
//            return NAME;
//        }
//    }
//
//    @Override
//    protected Mono<Object> determineCurrentLookupKey() {
//        return ReactiveTenantContextHolder.getContext()
//            .map(TenantContext::getTenant)
//            .map(Tenant::getId)
//            .cast(Object.class)
//            .transform(m -> ReactorUtils.errorIfEmpty(m, () -> new RuntimeException("ContextView does not contain the TenantContext")));
//    }
//
//    @Override
//    public ConnectionFactoryMetadata getMetadata() {
//        // If we don't override this method, it will try to determine the Dialect from the default
//        // ConnectionFactory. This is a problem, because you don't want a "Default ConnectionFactory"
//        // when you cannot resolve the Tenant.
//        //
//        // That's why we explicitly return a fixed PostgresqlConnectionFactoryMetadata. This class
//        // is also defined within the r2dbc library, but it isn't exposed to public.
//        return PostgresqlConnectionFactoryMetadata.INSTANCE;
//    }
//}
