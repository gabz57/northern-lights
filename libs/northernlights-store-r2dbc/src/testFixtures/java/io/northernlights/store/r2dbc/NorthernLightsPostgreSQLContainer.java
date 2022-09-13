package io.northernlights.store.r2dbc;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class NorthernLightsPostgreSQLContainer extends PostgreSQLContainer<NorthernLightsPostgreSQLContainer> {

    public NorthernLightsPostgreSQLContainer() {
        super(DockerImageName.parse("debezium/postgres:12-alpine")
            .asCompatibleSubstituteFor("postgres"));
        withUrlParam("TC_IMAGE_TAG", "12.10")
            .withUrlParam("TC_DAEMON", "true");
        withDatabaseName("northernlights-chat")
            .withUsername("postgres")
            .withPassword("postgres")
            .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust");
        // To enable 'logical' WAL level on 'postgres:12.10'
        // super("postgres:12.10");
        // withCommand("postgres -c wal_level=logical")
        // withCopyFileToContainer(MountableFile.forClasspathResource("postgresql.conf"), "/etc/postgresql/postgresql.conf");

        // To complete with additional script(s)
        // withInitScript("db/setup.sql");
    }

    @Override
    public void start() {
        super.start();

        // Re-exposing properties from PostgreSQL TestContainer
        System.setProperty("chat.store.r2dbc.url", "r2dbc:postgresql://" + getUsername() + "@" + getHost() + ":" + getFirstMappedPort() + "/" + getDatabaseName());
        System.setProperty("chat.store.r2dbc.username", getUsername());
        System.setProperty("chat.store.r2dbc.password", getPassword());

        // Re-exposing properties for Debezium
        System.setProperty("database.hostname", getHost());
        System.setProperty("database.port", getFirstMappedPort().toString());
        System.setProperty("database.user", getUsername());
        System.setProperty("database.password", getPassword());
        System.setProperty("database.dbname", getDatabaseName());
        System.setProperty("offset.storage.file.filename", "");
        System.setProperty("database.history.file.filename", "");
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
//        super.stop();
    }
}
