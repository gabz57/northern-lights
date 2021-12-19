package io.northernlights.store.r2dbc;

import org.testcontainers.containers.PostgreSQLContainer;

public class NorthernLightsPostgreSQLContainer extends PostgreSQLContainer<NorthernLightsPostgreSQLContainer> {

    public NorthernLightsPostgreSQLContainer() {
        super("postgres:12.10");
        withDatabaseName("northernlights-chat");
        withUrlParam("TC_IMAGE_TAG", "12.10");
        withUrlParam("TC_DAEMON", "true");
        withUsername("postgres");
        withPassword("postgres");
        withEnv("POSTGRES_HOST_AUTH_METHOD", "trust");
//        withInitScript("db/setup.sql");
    }

    @Override
    public void start() {
        super.start();

        // Re-exposing properties from PostgreSQL TestContainer
        System.setProperty("chat.store.r2dbc.url", "r2dbc:postgresql://" + getUsername() + "@" + getHost() + ":" + getFirstMappedPort() + "/" + getDatabaseName());
        System.setProperty("chat.store.r2dbc.username", getUsername());
        System.setProperty("chat.store.r2dbc.password", getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
//        super.stop();
    }
}
