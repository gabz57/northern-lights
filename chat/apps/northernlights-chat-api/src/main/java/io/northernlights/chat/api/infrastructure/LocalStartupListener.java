package io.northernlights.chat.api.infrastructure;

import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.store.chatter.ChatterStore;
import io.northernlights.chat.store.conversation.ConversationStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class LocalStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final OffsetDateTime CREATION_DATE_TIME = OffsetDateTime.of(2020, 4, 2, 13, 45, 25, 0, ZoneOffset.UTC);

    private static final Map<String, Chatter> DUMMY_CHATTERS = Map.of(
        "Alpha", new Chatter(ChatterId.of(UUID.fromString("3551ba8c-ef09-4459-9c4d-39c896b1ce6d")), "Alpha"),
        "Beta", new Chatter(ChatterId.of(UUID.fromString("bb0188da-606a-4198-894b-56ba002721c4")), "Beta"),
        "Gamma", new Chatter(ChatterId.of(UUID.fromString("804d41f7-6782-464b-a196-2907846b9998")), "Gamma"),
        "Delta", new Chatter(ChatterId.of(UUID.fromString("1be26bb1-f5c8-418d-a0a2-b432b30c5bf3")), "Delta")
    );

    private final ChatterStore chatterStore;
    private final ConversationStore conversationStore;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initChatters();
        initConversations();
    }

    private void initChatters() {
        log.info("Creating chatters");
        boolean empty = chatterStore.listChatters(DUMMY_CHATTERS.values().stream().map(Chatter::getChatterID).toList()).block().isEmpty();
        if (empty) {
            Mono.empty()
                .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("Alpha")))
                .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("Beta")))
                .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("Gamma")))
                .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("Delta")))
                .subscribe();
        }
    }

    private void initConversations() {
        log.info("Creating conversations");
        boolean empty = chatterStore.listConversationIds(DUMMY_CHATTERS.get("Alpha").getChatterID()).block().isEmpty();
        if (empty) {
            Mono.empty()
                .then(conversationStore.create(CREATION_DATE_TIME,
                    DUMMY_CHATTERS.get("Alpha").getChatterID(),
                    "First conversation ! Yay !!",
                    List.of(
                        DUMMY_CHATTERS.get("Beta").getChatterID(),
                        DUMMY_CHATTERS.get("Gamma").getChatterID(),
                        DUMMY_CHATTERS.get("Delta").getChatterID()
                    ),
                    false))
                .flatMap(chatterStore::writeConversationCreated)

                .then(conversationStore.create(CREATION_DATE_TIME,
                    DUMMY_CHATTERS.get("Alpha").getChatterID(),
                    "One On One",
                    List.of(
                        DUMMY_CHATTERS.get("Beta").getChatterID()
                    ),
                    true))
                .flatMap(chatterStore::writeConversationCreated)

                .subscribe();
        }
    }
}
