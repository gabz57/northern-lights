package io.northernlights.chat.store.r2dbc.chatter;

import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.store.chatter.ChatterStore;
import io.northernlights.chat.store.r2dbc.ChatStoreIntegrationTestBase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public class R2dbcChatterStoreTest extends ChatStoreIntegrationTestBase {

    private static final ChatterId CONVERSATION_CREATOR_CHATTER_ID = ChatterId.of(UUID.randomUUID());
    private static final ChatterId INVITED_CHATTER_ID_1 = ChatterId.of(UUID.randomUUID());
    private static final ChatterId INVITED_CHATTER_ID_2 = ChatterId.of(UUID.randomUUID());
    private static final ChatterId INVITED_CHATTER_ID_3 = ChatterId.of(UUID.randomUUID());

    @Autowired
    private ChatterStore chatterStore;

    @Nested
    public class insertAndListChatters {
        @Test
        void should_find_all_expected_chatters() {
            // Given
            Mono<Void> createChatters = chatterStore.insertChatter(new Chatter(CONVERSATION_CREATOR_CHATTER_ID, CONVERSATION_CREATOR_CHATTER_ID.getId().toString()))
                .then(chatterStore.insertChatter(new Chatter(INVITED_CHATTER_ID_1, INVITED_CHATTER_ID_1.getId().toString())))
                .then(chatterStore.insertChatter(new Chatter(INVITED_CHATTER_ID_2, INVITED_CHATTER_ID_2.getId().toString())))
                .then(chatterStore.insertChatter(new Chatter(INVITED_CHATTER_ID_3, INVITED_CHATTER_ID_3.getId().toString())))
                .then();

            // When
            Mono<List<Chatter>> chatters = createChatters.then(chatterStore.listChatters(List.of(
                INVITED_CHATTER_ID_1,
                INVITED_CHATTER_ID_2
            )));

            // Then
            chatters.as(StepVerifier::create)
                .expectNext(List.of(
                    new Chatter(INVITED_CHATTER_ID_1, INVITED_CHATTER_ID_1.getId().toString()),
                    new Chatter(INVITED_CHATTER_ID_2, INVITED_CHATTER_ID_2.getId().toString())
                ))
                .verifyComplete();
        }
    }
}
