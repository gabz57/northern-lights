package io.northernlights.store.r2dbc;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@TestConfiguration
public class DBMigrationTestConfiguration {
    @Bean
    @LiquibaseDataSource
    public DataSource liquibaseDataSource() {
        return DataSourceBuilder.create()
            .url(System.getProperty("spring.datasource.url"))
            .username(System.getProperty("spring.datasource.username"))
            .password(System.getProperty("spring.datasource.password"))
            .driverClassName(System.getProperty("spring.datasource.driver-class-name"))
            .build();
    }
}
