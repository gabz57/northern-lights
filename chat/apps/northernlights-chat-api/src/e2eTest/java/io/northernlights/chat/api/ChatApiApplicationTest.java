package io.northernlights.chat.api;

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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static io.northernlights.chat.api.infrastructure.client.http.ServerSentEventAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ChatApiApplicationTest extends E2ETestBase {
    private static final ParameterizedTypeReference<ServerSentEvent<SseChatPayload>> SSE_CHAT_PAYLOAD_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    @Autowired
    private ChatterStore chatterStore;

    private static final Map<String, Chatter> DUMMY_CHATTERS = Map.of(
        "Alpha", new Chatter(ChatterId.of(UUID.fromString("3551ba8c-ef09-4459-9c4d-39c896b1ce6d")), "Alpha"),
        "Beta", new Chatter(ChatterId.of(UUID.fromString("bb0188da-606a-4198-894b-56ba002721c4")), "Beta"),
        "Gamma", new Chatter(ChatterId.of(UUID.fromString("804d41f7-6782-464b-a196-2907846b9998")), "Gamma"),
        "Delta", new Chatter(ChatterId.of(UUID.fromString("1be26bb1-f5c8-418d-a0a2-b432b30c5bf3")), "Delta")
    );

    @Test
    void contextLoads() {
        boolean actual = true;
        assertThat(actual).isTrue();
    }

    private void setupChatters() {
        System.out.println("SETUP CHATTERS");
        Mono.empty()
            .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("Alpha")))
            .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("Beta")))
            .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("Gamma")))
            .then(chatterStore.insertChatter(DUMMY_CHATTERS.get("Delta")))
            .then()
            .as(StepVerifier::create)
            .verifyComplete();
        System.out.println("SETUP CHATTERS DONE");
    }

    @Test
    void demo() {
        setupChatters();

        AtomicReference<String> sseChatKey = new AtomicReference<>();
        AtomicReference<String> firstConversationId = new AtomicReference<>();

        authenticateSse(DUMMY_CHATTERS.get("Alpha"))
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
            .consumeNextWith(n -> assertThat(n.event()).isEqualTo("Hello " + sseChatKey.get()))
            .then(() -> createConversation(DUMMY_CHATTERS.get("Beta"), "SSE TEST !!", DUMMY_CHATTERS.get("Alpha"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(CreateConversationResponse.class)
                .value(r -> assertThat(r.getConversationId()).isNotNull())
                .value(response -> firstConversationId.set(response.getConversationId()))
            )
            .consumeNextWith(n -> assertThat(n).hasEvent("CHATTER:INSTALL").hasData().withChatterData().hasName("Alpha"))
            .consumeNextWith(n -> assertThat(n).hasEvent("CHATTER:INSTALL").hasData().withChatterData().hasName("Beta"))
            .consumeNextWith(n -> assertThat(n).hasEvent("CONVERS:INSTALL").hasData().withConversationData().hasName("SSE TEST !!").hasDialogue(true))
            .then(() -> sendMessage(DUMMY_CHATTERS.get("Beta"), firstConversationId.get(), "Hello SSE world !!")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SendMessageResponse.class)
                .value(r -> assertThat(r.getConversationDataId()).isNotNull()))
            .consumeNextWith(n -> assertThat(n).hasEvent("CONVERS:UPDATE:MESSAGE").hasData().withConversationData())
            .consumeNextWith(n -> assertThat(n).hasEvent("CONVERS:UPDATE:READ_MARKER").hasData().withConversationData().withReadMarkers())
            .thenCancel()
            .verify();
    }

    private WebTestClient.RequestHeadersSpec<?> authenticateSse(Chatter chatter) {
        System.out.println("AUTHENTICATE");
        return this.client.post()
            .uri("/v1/chat/api/auth")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + chatter.getChatterID().getId().toString())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                    "conversationStatuses": {}
                }""");
    }

    private WebTestClient.RequestHeadersSpec<?> openSseClient(AtomicReference<String> sseChatKey) {
        System.out.println("-> OPEN SSE CLIENT");
        return this.client.get()
            .uri("/v1/chat/api/sse")
            .header("sse-chat-key", sseChatKey.get())
            .accept(MediaType.TEXT_EVENT_STREAM);
    }

    private WebTestClient.RequestHeadersSpec<?> createConversation(Chatter creator, String conversationName, Chatter invited) {
        System.out.println("CREATE CONVERSATION");
        return this.client.post()
            .uri("/v1/chat/api/open")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + creator.getChatterID().getId().toString())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\n" +
                       "    \"name\": \"" + conversationName + "\",\n" +
                       "    \"participants\": [\"" + invited.getChatterID().getId().toString() + "\"],\n" +
                       "    \"dialogue\": true\n" +
                       "}");
    }

    private WebTestClient.RequestHeadersSpec<?> sendMessage(Chatter chatter, String conversationId, String message) {
        System.out.println("SEND MESSAGE");
        return this.client.post()
            .uri("/v1/chat/api/message")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + chatter.getChatterID().getId().toString())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                    "conversationId": "CONVERSATION_ID",
                    "message": "MESSAGE"
                }"""
                .replace("CONVERSATION_ID", conversationId)
                .replace("MESSAGE", message));
    }
}
