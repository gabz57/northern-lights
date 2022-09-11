package io.northernlights.chat.event.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class DebeziumConfiguration {

    @Value("${connector.class}")
    private String connectorClass;

    @Value("${database.hostname}")
    private String databaseHostname;

    @Value("${database.port}")
    private String databasePort;

    @Value("${database.dbname}")
    private String databaseDbname;

    @Value("${database.user}")
    private String databaseUser;

    @Value("${database.password}")
    private String databasePassword;

    @Value("${database.server.name}")
    private String databaseServerName;

    @Value("${schema.include.list}")
    private String schemaIncludeList;

    @Value("${table.include.list}")
    private String tableIncludeList;

    @Value("${offset.storage.file.filename}")
    private String offsetStorageFileFilename;

    @Value("${database.history.file.filename}")
    private String databaseHistoryFileFilename;

    @Bean
    public io.debezium.config.Configuration createConnectorConfig() throws IOException {
        if (offsetStorageFileFilename.isEmpty()) {
            File offsetStorageTempFile = File.createTempFile("offsets_", ".dat");
            offsetStorageFileFilename = offsetStorageTempFile.getAbsolutePath();
        }
        if (databaseHistoryFileFilename.isEmpty()) {
            File dbHistoryTempFile = File.createTempFile("dbhistory_", ".dat");
            databaseHistoryFileFilename = dbHistoryTempFile.getAbsolutePath();
        }
        return io.debezium.config.Configuration.create()
            .with("name", "chat-event-debezium")
            .with("connector.class", connectorClass)
            .with("database.dbname", databaseDbname)
            .with("database.hostname", databaseHostname)
            //.with("database.name", databaseName)
            .with("database.port", databasePort)
            .with("database.user", databaseUser)
            .with("database.password", databasePassword)
            //.with("database.server.id", databaseServerId)
            .with("database.server.name", databaseServerName)
            //.with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
            //.with("database.history.file.filename", databaseHistoryFileFilename)
            .with("plugin.name", "pgoutput")
            .with("snapshot.mode", "initial")
            // .with("snapshot.mode", "always")
            .with("schema.include.list", schemaIncludeList)
            .with("table.include.list", tableIncludeList)
            .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
            .with("offset.flush.interval.ms", "5000")
            .with("offset.storage.file.filename", offsetStorageFileFilename)
            .build();
    }
}

