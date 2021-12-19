//package io.northernlights.chat.store.r2dbc;
//
//import io.r2dbc.spi.ConnectionFactory;
//import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
//import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
//import reactor.core.publisher.Mono;
//
//import javax.annotation.PostConstruct;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class TenantRoutingConnectionFactory extends AbstractRoutingConnectionFactory {
//
//    private final DatabaseMigrationService databaseMigrationService;
//    private final ConnectionFactoryBuilder connectionFactoryBuilder;
//
//    private final Map<String, ConnectionFactory> targetConnectionFactories = new ConcurrentHashMap<>();
//
//    @PostConstruct
//    private void init() {
//        setLenientFallback(false);
//        setTargetConnectionFactories(new HashMap<>());
//        setDefaultTargetConnectionFactory(connectionFactoryBuilder.buildConnectionFactory());
//    }
//
//    @Override
//    protected Mono<Object> determineCurrentLookupKey() {
//        return ReactiveSecurityContextHolder.getContext()
//            .map(this::getTenantFromContext)
//            .flatMap(tenant -> databaseMigrationService.migrateTenantIfNeeded(tenant)
//                .thenReturn(tenant));
//    }
//
//    private String getTenantFromContext(SecurityContext securityContext) {
//        String tenant = null;
//        Object principal = securityContext.getAuthentication().getPrincipal();
//        if (principal instanceof NorthernLightsPrincipal) {
//            NorthernLightsPrincipal northernLightsPrincipal = (NorthernLightsPrincipal) principal;
//            tenant = northernLightsPrincipal.getTenant();
//        }
//    ...
//        log.debug("Tenant resolved: " + tenant);
//        return tenant;
//    }
//
//    @Override
//    protected Mono<ConnectionFactory> determineTargetConnectionFactory() {
//        return determineCurrentLookupKey().map(k -> {
//            String key = (String) k;
//            if (!targetConnectionFactories.containsKey(key)) {
//                targetConnectionFactories.put(key, connectionFactoryBuilder.buildConnectionFactory(key));
//            }
//            return targetConnectionFactories.get(key);
//        });
//    }
