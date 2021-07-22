package io.northernlights.chat.api;

import io.northernlights.chat.api.infrastructure.client.http.SseChatPayload;
import io.northernlights.chat.api.infrastructure.conversation.http.model.ChatAuthenticationResponse;
import io.northernlights.chat.api.infrastructure.conversation.http.model.CreateConversationResponse;
import io.northernlights.chat.api.infrastructure.conversation.http.model.SendMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ChatApiApplicationTest extends E2ETestBase {
    private static final ParameterizedTypeReference<ServerSentEvent<SseChatPayload>> SSE_CHAT_PAYLOAD_TYPE_REF = new ParameterizedTypeReference<>() {
    };
    @Autowired
    protected WebTestClient client;

    @Test
    void contextLoads() {
        boolean actual = true;
        assertThat(actual).isTrue();
    }

    @Test
    void demo() {
        AtomicReference<String> sseChatKey = new AtomicReference<>();
        AtomicReference<String> firstConversationId = new AtomicReference<>();

        this.client.post()
            .uri("/v1/chat/api/auth")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                    "conversationStatuses": {}
                }""")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(ChatAuthenticationResponse.class)
            .value(response -> assertThat(response.getSseChatKey()).isNotNull())
            .value(response -> sseChatKey.set(response.getSseChatKey()));

        Flux<ServerSentEvent<SseChatPayload>> sseFlow = this.client.get()
            .uri("/v1/chat/api/sse")
            .header("sse-chat-key", sseChatKey.get())
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus().isOk()
            .returnResult(SSE_CHAT_PAYLOAD_TYPE_REF)
            .getResponseBody();

        StepVerifier.create(sseFlow)
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
            .then(() -> this.client.post()
                .uri("/v1/chat/api/open")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + "2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "name": "Ma première conversation",
                        "participants": ["1"]
                    }""")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(CreateConversationResponse.class)
                .value(r -> assertThat(r.getConversationId()).isNotNull())
                .value(response -> firstConversationId.set(response.getConversationId())))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .consumeNextWith(n -> log.info("{}", n))
            .then(() -> this.client.post()
                .uri("/v1/chat/api/message")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + "2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "conversationId": "CONVERSATION_ID",
                        "message": "BLOU BLOU"
                    }""".replace("CONVERSATION_ID", firstConversationId.get()))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SendMessageResponse.class)
                .value(r -> assertThat(r.getConversationDataId()).isNotNull()))
            .consumeNextWith(n -> log.info("{}", n))
            .thenCancel()
            .verify();
    }

}
