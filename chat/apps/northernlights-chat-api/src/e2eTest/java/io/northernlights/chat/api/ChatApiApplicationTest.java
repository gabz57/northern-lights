package io.northernlights.chat.api;

import io.northernlights.chat.api.application.conversation.ChatCommands;
import io.northernlights.chat.api.application.conversation.CreateConversationCommand;
import io.northernlights.chat.api.infrastructure.client.http.SseChatPayload;
import io.northernlights.chat.api.infrastructure.conversation.http.model.ChatAuthenticationResponse;
import io.northernlights.chat.api.infrastructure.conversation.http.model.CreateConversationResponse;
import io.northernlights.chat.api.infrastructure.conversation.http.model.SendMessageResponse;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.store.chatter.ChatterStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ChatApiApplicationTest extends E2ETestBase {
    private static final ParameterizedTypeReference<ServerSentEvent<SseChatPayload>> SSE_CHAT_PAYLOAD_TYPE_REF = new ParameterizedTypeReference<>() {
    };
    @Autowired
    protected WebTestClient client;

    @Autowired
    private ChatterStore chatterStore;
    @Autowired
    private ChatCommands chatCommands;

    private static final Map<String, Chatter> DUMMY_CHATTERS = Map.of(
        "1", new Chatter(ChatterId.of(UUID.fromString("3551ba8c-ef09-4459-9c4d-39c896b1ce6d")), "Alpha"),
        "2", new Chatter(ChatterId.of(UUID.fromString("bb0188da-606a-4198-894b-56ba002721c4")), "Beta"),
        "3", new Chatter(ChatterId.of(UUID.fromString("804d41f7-6782-464b-a196-2907846b9998")), "Gamma"),
        "4", new Chatter(ChatterId.of(UUID.fromString("1be26bb1-f5c8-418d-a0a2-b432b30c5bf3")), "Delta")
    );

    @Test
    void contextLoads() {
        boolean actual = true;
        assertThat(actual).isTrue();
    }

    @Test
    void demo() {
        setupDemo();

        System.out.println("DEMO READY");

        AtomicReference<String> sseChatKey = new AtomicReference<>();
        AtomicReference<String> firstConversationId = new AtomicReference<>();

        authenticateSse()
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(ChatAuthenticationResponse.class)
            .value(response -> assertThat(response.getSseChatKey()).isNotNull())
            .value(response -> sseChatKey.set(response.getSseChatKey()));

        openSseClient(sseChatKey)
            .exchange()
            .expectStatus().isOk()
            .returnResult(SSE_CHAT_PAYLOAD_TYPE_REF)
            .getResponseBody()
            .as(StepVerifier::create)
            .expectSubscription()
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .then(() -> createConversation()
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(CreateConversationResponse.class)
                .value(r -> assertThat(r.getConversationId()).isNotNull())
                .value(response -> firstConversationId.set(response.getConversationId()))
            )
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .then(() -> sendMessage(firstConversationId.get())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SendMessageResponse.class)
                .value(r -> assertThat(r.getConversationDataId()).isNotNull()))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .thenCancel()
            .verify();
    }

    private void setupDemo() {
        Mono.empty()
            .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("1")))
            .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("2")))
            .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("3")))
            .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("4")))
            .then()
            .as(StepVerifier::create)
            .verifyComplete();

        Mono.empty()
            .then(createConversation("Conversation 1", DUMMY_CHATTERS.get("1"), List.of(DUMMY_CHATTERS.get("1"), DUMMY_CHATTERS.get("2")), false))
            .then(createConversation("Conversation 2", DUMMY_CHATTERS.get("1"), List.of(DUMMY_CHATTERS.get("1"), DUMMY_CHATTERS.get("3"), DUMMY_CHATTERS.get("4")), false))
            .then(createConversation("Conversation 3", DUMMY_CHATTERS.get("2"), List.of(DUMMY_CHATTERS.get("2"), DUMMY_CHATTERS.get("3")), false))
            .then(createConversation("Conversation 4", DUMMY_CHATTERS.get("2"), List.of(DUMMY_CHATTERS.get("2"), DUMMY_CHATTERS.get("4")), false))
            .then()
            .as(StepVerifier::create)
            .verifyComplete();
    }

    private WebTestClient.RequestHeadersSpec<?> createConversation() {
        System.out.println("CREATE CONVERSATION");
        return this.client.post()
            .uri("/v1/chat/api/open")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + DUMMY_CHATTERS.get("2").getChatterID().getId().toString())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\n" +
                       "    \"name\": \"Ma première conversation\",\n" +
                       "    \"participants\": [\"" + DUMMY_CHATTERS.get("1").getChatterID().getId().toString() + "\"],\n" +
                       "    \"dialogue\": true\n" +
                       "}");
    }

    private WebTestClient.RequestHeadersSpec<?> sendMessage(String conversationId) {
        System.out.println("SEND MESSAGE");
        return this.client.post()
            .uri("/v1/chat/api/message")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + DUMMY_CHATTERS.get("2").getChatterID().getId().toString())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                    "conversationId": "CONVERSATION_ID",
                    "message": "BLOU BLOU"
                }""".replace("CONVERSATION_ID", conversationId));
    }

    private WebTestClient.RequestHeadersSpec<?> openSseClient(AtomicReference<String> sseChatKey) {
        System.out.println("-> OPEN SSE CLIENT");
        return this.client.get()
            .uri("/v1/chat/api/sse")
            .header("sse-chat-key", sseChatKey.get())
            .accept(MediaType.TEXT_EVENT_STREAM);
    }

    private WebTestClient.RequestHeadersSpec<?> authenticateSse() {
        System.out.println("AUTHENTICATE");
        return this.client.post()
            .uri("/v1/chat/api/auth")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + DUMMY_CHATTERS.get("1").getChatterID().getId().toString())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                    "conversationStatuses": {}
                }""");
    }

    private Mono<CreateConversationCommand.CreateConversationCommandResult> createConversation(String conversationName, Chatter author, List<Chatter> allParticipants, Boolean dialogue) {
        return chatCommands.createConversation().execute(CreateConversationCommand.CreateConversationCommandInput.builder()
            .conversationName(conversationName)
            .creator(author.getChatterID())
            .participants(allParticipants.stream().map(Chatter::getChatterID).toList())
            .dialogue(dialogue)
            .build());
    }
}
