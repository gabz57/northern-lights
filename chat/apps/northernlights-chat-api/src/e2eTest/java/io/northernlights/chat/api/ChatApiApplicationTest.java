package io.northernlights.chat.api;

import io.northernlights.chat.api.infrastructure.client.http.SseChatPayload;
import io.northernlights.chat.api.infrastructure.conversation.http.model.InitializeSseChatResponse;
import io.northernlights.chat.api.infrastructure.conversation.http.model.CreateConversationResponse;
import io.northernlights.chat.api.infrastructure.conversation.http.model.MarkAsReadResponse;
import io.northernlights.chat.api.infrastructure.conversation.http.model.SendMessageResponse;
import io.northernlights.chat.api.infrastructure.user.http.model.SubscribeUserResponse;
import io.northernlights.chat.api.infrastructure.user.http.model.UserInfoResponse;
import io.northernlights.chat.domain.model.chatter.Chatter;
import io.northernlights.chat.domain.model.chatter.ChatterId;
import io.northernlights.chat.domain.store.chatter.ChatterStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static io.northernlights.chat.api.infrastructure.client.http.ServerSentEventAssert.assertThat;
import static io.northernlights.chat.domain.ApiConstants.*;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ChatApiApplicationTest extends E2ETestBase {
    private static final ParameterizedTypeReference<ServerSentEvent<SseChatPayload>> SSE_CHAT_PAYLOAD_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    private static final String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImNlYzEzZGViZjRiOTY0Nzk2ODM3MzYyMDUwODI0NjZjMTQ3OTdiZDAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJuYmYiOjE2NDk1MDg4OTQsImF1ZCI6IjE4MzE0NDU0NDU2NS02cGMzZ2s1ZXI1NDE5bW80Z2owYTRkOGV2ODdpaWdrZy5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExNDg1Nzc1NDc1MTA1MzcwMjE2NyIsImVtYWlsIjoiYXJuYXVkLnNjaHdhcnR6LjU3QGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiIxODMxNDQ1NDQ1NjUtNnBjM2drNWVyNTQxOW1vNGdqMGE0ZDhldjg3aWlna2cuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJuYW1lIjoiQXJuYXVkIFNjaHdhcnR6IiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FBVFhBSnhMcGY0RTdCd0d6Ykp3cGl4LTFPdlFMR2wwZVJJTUM0OVFqYlJ5PXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6IkFybmF1ZCIsImZhbWlseV9uYW1lIjoiU2Nod2FydHoiLCJpYXQiOjE2NDk1MDkxOTQsImV4cCI6MTY0OTUxMjc5NCwianRpIjoiZDY3NmQzNjBlZmJmNmQ5OGExNzAzNjQxZmJhZTNlMmI0MzA3MmY2MSJ9.iG-_dOTxmlF7Nr_NelcnYAX2cZSstGG8PwZFBGQCu63BLI131lW_PB6IXdp4anOX8KjuG_rPghPSi_9cV938I2DdpI0m_7B7vUuoYPpzxl-KtuU3nw4122lgxn4sTcH0H9YqhDFQf6nbMjLlDmQ5s1NEv5NW5WHd3Xbe1P0wJp8MY1e-vAwE-a7vU-UrbQICjwnxykSvDLCPoSs4tuUnZhlP8yrVqn44quxVRuR6xsIm9ElALMF-0USof4kSXCAa6GfNiIW5MA8UV6aOU0aVFEZU4teZcrbpZeIALSXklvxrMJyoreeVdFUbJM3HwYV5HYkbd_AyJXtDamyWnSIIJg";

    @Autowired
    private ChatterStore chatterStore;

    private static final Map<String, Chatter> DUMMY_CHATTERS = Map.of(
        "Alpha", new Chatter(ChatterId.of(UUID.fromString("3551ba8c-ef09-4459-9c4d-39c896b1ce6d")), "Alpha"),
        "Beta", new Chatter(ChatterId.of(UUID.fromString("bb0188da-606a-4198-894b-56ba002721c4")), "Beta"),
        "Gamma", new Chatter(ChatterId.of(UUID.fromString("804d41f7-6782-464b-a196-2907846b9998")), "Gamma"),
        "Delta", new Chatter(ChatterId.of(UUID.fromString("1be26bb1-f5c8-418d-a0a2-b432b30c5bf3")), "Delta")
    );

    @Order(0)
    @Test
    void contextLoads() {
        boolean actual = true;
        assertThat(actual).isTrue();
    }

    @Order(1)
    @Test
    void userInfo_without_subscribed_account_should_return_403() {
        userInfo(token)
            .exchange()
            .expectStatus().isForbidden()
            .expectBody()
            .consumeWith(response ->
                assertThat(response.getResponseBody()).asString().isEqualTo("Access Denied"));
    }

    @Order(1)
    @Test
    void initializeSse_without_subscribed_account_should_return_403() {
        initializeSse(token)
            .exchange()
            .expectStatus().isForbidden()
            .expectBody()
            .consumeWith(response ->
                assertThat(response.getResponseBody()).asString().isEqualTo("Access Denied"));
    }

    @Order(1)
    @Test
    void initializeSse_with_invalid_token_return_401() {
        initializeSse("token")
            .exchange()
            .expectStatus().is4xxClientError()
            .expectBody()
            .json("{}");
    }


    @Order(1)
    @Test
    void initializeSse_without_token_return_401() {
        this.client.post()
            .uri(CHAT_API_INITIALIZE_SSE)
//            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                    "conversationStatuses": {}
                }""")
            .exchange()
            .expectStatus().is4xxClientError()
            .expectBody()
            .isEmpty();
    }

    @Order(2)
    @Test
    void demo() {
        // The Token can't be used before Sat Apr 09 12:59:54 UTC 2022.
        // The Token has expired on       Sat Apr 09 13:59:54 UTC 2022.
        timeService.setNow(ZonedDateTime.of(LocalDate.of(2022, 4, 9), LocalTime.of(13, 35, 20), ZoneOffset.UTC));

        setupChatters();

        AtomicReference<ChatterId> chatterId = new AtomicReference<>();
        String sseChatKey = initSse(chatterId);

        AtomicReference<String> firstConversationId = new AtomicReference<>();
        AtomicReference<String> firstConversationDataId = new AtomicReference<>();
        openSseClient(token, sseChatKey)
            .exchange()
            .expectStatus().isOk()
            .returnResult(SSE_CHAT_PAYLOAD_TYPE_REF) // deserialization Json -> POJO
            .getResponseBody()
            .as(StepVerifier::create)
            .expectSubscription()
            .consumeNextWith(n -> assertThat(n.event()).isEqualTo("Hello " + sseChatKey))

            .then(() -> createConversation(token, "SSE TEST !!", List.of(chatterId.get(), DUMMY_CHATTERS.get("Beta").getChatterId()))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(CreateConversationResponse.class)
                .value(r -> assertThat(r.getConversationId()).isNotNull())
                .value(response -> firstConversationId.set(response.getConversationId()))
            )
            .consumeNextWith(n -> assertThat(n).hasEvent("CHATTER:INSTALL").hasData().withChatterData().hasId(DUMMY_CHATTERS.get("Alpha").getChatterId()))
            .consumeNextWith(n -> assertThat(n).hasEvent("CHATTER:INSTALL").hasData().withChatterData().hasId(DUMMY_CHATTERS.get("Beta").getChatterId()))
            .consumeNextWith(n -> assertThat(n).hasEvent("CHATTER:INSTALL").hasData().withChatterData().hasId(DUMMY_CHATTERS.get("Gamma").getChatterId()))
            .consumeNextWith(n -> assertThat(n).hasEvent("CHATTER:INSTALL").hasData().withChatterData().hasId(DUMMY_CHATTERS.get("Delta").getChatterId()))
            .consumeNextWith(n -> assertThat(n).hasEvent("CHATTER:INSTALL").hasData().withChatterData().hasId(chatterId.get()))
            .consumeNextWith(n -> assertThat(n).hasEvent("CONVERS:INSTALL").hasData().withConversationData().hasName("SELF").isPrivate(true))
            .consumeNextWith(n -> assertThat(n).hasEvent("CONVERS:INSTALL").hasData().withConversationData().hasName("SSE TEST !!").isPrivate(true))

            .then(() -> sendMessage(token, firstConversationId.get(), "Hello SSE world !!")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SendMessageResponse.class)
                .value(r -> {
                    assertThat(r.getConversationDataId()).isNotNull();
                    firstConversationDataId.set(r.getConversationDataId());
                }))
            .consumeNextWith(n -> assertThat(n).hasEvent("CONVERS:UPDATE:MESSAGE").hasData().withConversationData())

            .then(() -> markAsRead(token, firstConversationId.get(), firstConversationDataId.get())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(MarkAsReadResponse.class)
                .value(r -> assertThat(r.getConversationDataId()).isNotNull()))
            .consumeNextWith(n -> assertThat(n).hasEvent("CONVERS:UPDATE:READ_MARKER").hasData().withConversationData().withReadMarkers())

            .thenCancel()
            .verify();
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

    private String initSse(AtomicReference<ChatterId> chatterId) {
        AtomicReference<String> sseChatKey = new AtomicReference<>();

        subscribeUser(token)
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(SubscribeUserResponse.class)
            .value(response -> assertThat(response.getChatterId()).isNotNull());

        userInfo(token)
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(UserInfoResponse.class)
            .value(response -> assertThat(response.getChatterId()).isNotNull())
            .value(response -> chatterId.set(ChatterId.of(response.getChatterId())));

        initializeSse(token)
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(InitializeSseChatResponse.class)
            .value(response -> assertThat(response.getSseChatKey()).isNotNull())
            .value(response -> sseChatKey.set(response.getSseChatKey()));

        return sseChatKey.get();
    }

    private WebTestClient.RequestHeadersSpec<?> subscribeUser(String token) {
        System.out.println("SUBSCRIBE USER");
        return this.client.post()
            .uri(USER_API_SUBSCRIBE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);
    }

    private WebTestClient.RequestHeadersSpec<?> userInfo(String token) {
        System.out.println("USER INFO");
        return this.client.get()
            .uri(USER_API_INFO)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON);
    }

    private WebTestClient.RequestHeadersSpec<?> initializeSse(String token) {
        System.out.println("INITIALIZE SSE");
        return this.client.post()
            .uri(CHAT_API_INITIALIZE_SSE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                    "conversationStatuses": {}
                }""");
    }

    private WebTestClient.RequestHeadersSpec<?> openSseClient(String token, String sseChatKey) {
        System.out.println("-> OPEN SSE CLIENT");
        return this.client.get()
            .uri(CHAT_API_SSE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .header("sse-chat-key", sseChatKey)
            .accept(MediaType.TEXT_EVENT_STREAM);
    }

    private WebTestClient.RequestHeadersSpec<?> createConversation(String token, String conversationName, List<ChatterId> inviteds) {
        System.out.println("CREATE CONVERSATION");
        return this.client.post()
            .uri(CHAT_API_OPEN)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\n" +
                       "    \"name\": \"" + conversationName + "\",\n" +
                       "    \"participants\": [\"" + inviteds.stream().map(ChatterId::getId).map(UUID::toString).collect(joining("\",\"")) + "\"],\n" +
                       "    \"dialogue\": true\n" +
                       "}");
    }

    private WebTestClient.RequestHeadersSpec<?> sendMessage(String token, String conversationId, String message) {
        System.out.println("SEND MESSAGE");
        return this.client.post()
            .uri(CHAT_API_MESSAGE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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
    private WebTestClient.RequestHeadersSpec<?> markAsRead(String token, String conversationId, String conversationDataId) {
        System.out.println("MARK AS READ");
        return this.client.post()
            .uri(CHAT_API_MARK_AS_READ)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                    "conversationId": "CONVERSATION_ID",
                    "conversationDataId": "CONVERSATION_DATA_ID"
                }"""
                .replace("CONVERSATION_ID", conversationId)
                .replace("CONVERSATION_DATA_ID", conversationDataId));
    }
}
