package io.northernlights.store.r2dbc;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

public class NorthernLightsR2dbcExtension implements Extension, BeforeAllCallback, AfterAllCallback {
    private final NorthernLightsPostgreSQLContainer postgreSQLContainer = new NorthernLightsPostgreSQLContainer();

    @Override
    public void beforeAll(ExtensionContext context) {
        postgreSQLContainer.start();

        // Specific for initiating DB provided by TestContainer
        System.setProperty("spring.liquibase.enabled", "true");
        System.setProperty("spring.liquibase.change-log", "classpath:/db/changelog/db.changelog-master.xml");
        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
        System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
    }

    @Override
    public void afterAll(ExtensionContext context) {
        postgreSQLContainer.stop();
    }
}
