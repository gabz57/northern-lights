package io.northernlights.store.r2dbc;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Import({
    R2dbcAuditAwareTestConfiguration.class,
    DBMigrationTestConfiguration.class,
})
@ExtendWith(NorthernLightsR2dbcExtension.class)
@DataR2dbcTest
public class StoreIntegrationTestBase {
}
