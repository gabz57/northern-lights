package io.northernlights.chat.event.publisher;

import io.northernlights.chat.domain.event.store.ChatEventStore;
import io.northernlights.chat.event.publisher.TestableEventStoreConfiguration.Receiver;
import io.northernlights.chat.store.r2dbc.R2dbcChatStoreConfiguration;
import io.northernlights.chat.store.r2dbc.StoreCleaner;
import io.northernlights.chat.store.r2dbc.TestableStoreConfiguration;
import io.northernlights.chat.store.r2dbc.conversation.ConversationStoreConfigurationR2dbc;
import io.northernlights.chat.store.r2dbc.event.ChatEventStoreConfigurationR2dbc;
import io.northernlights.commons.TestableTimeConfiguration;
import io.northernlights.commons.TestableTimeConfiguration.TestTimeService;
import io.northernlights.store.r2dbc.DBMigrationTestConfiguration;
import io.northernlights.store.r2dbc.NorthernLightsR2dbcExtension;
import io.northernlights.store.r2dbc.R2dbcAuditAwareTestConfiguration;
import io.northernlights.store.redis.NorthernLightsRedisExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ActiveProfiles({"test"})
//@ActiveProfiles({"local", "test"})
//@DirtiesContext
@ExtendWith(NorthernLightsRedisExtension.class)
@ExtendWith(NorthernLightsR2dbcExtension.class)
@Import({
    R2dbcAuditAwareTestConfiguration.class,
    DBMigrationTestConfiguration.class,
    ConversationStoreConfigurationR2dbc.class,
    R2dbcChatStoreConfiguration.class,
    ChatEventStoreConfigurationR2dbc.class
})
@SpringBootTest(webEnvironment = NONE, classes = {
    TestableTimeConfiguration.class,
    TestableStoreConfiguration.class,
    TestableEventStoreConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatApiEventPublisherIntegrationTestBase {

    @Autowired
    protected TestTimeService timeService;
    @Autowired
    protected Receiver receiver;
    @Autowired
    protected ChatEventStore chatEventStore;
    @Autowired
    private StoreCleaner storeCleaner;

    @BeforeEach
    void clearDatabaseBefore() {
        receiver.reset();
        storeCleaner.clearDatabase();
    }
}
