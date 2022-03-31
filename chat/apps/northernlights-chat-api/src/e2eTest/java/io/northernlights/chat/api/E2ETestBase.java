package io.northernlights.chat.api;

import io.northernlights.store.r2dbc.DBMigrationTestConfiguration;
import io.northernlights.store.r2dbc.NorthernLightsR2dbcExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("local")
@DirtiesContext
@ExtendWith(NorthernLightsR2dbcExtension.class)
@Import(DBMigrationTestConfiguration.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public class E2ETestBase {
    @Autowired
    protected WebTestClient client;
}
