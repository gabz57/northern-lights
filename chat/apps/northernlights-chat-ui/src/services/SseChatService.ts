import { EventSourcePolyfill } from "event-source-polyfill";
import type {
  ChatterId,
  Conversation,
  ConversationDataId,
  ConversationPart,
  ReadMarkers,
} from "@/domain/model";
import { config } from "@/services/EnvConfig";
import type { ConversationsStore } from "@/stores/conversations";
import type { ChattersStore } from "@/stores/chatter";
import type { UserStore } from "@/stores/user";

export default class SseChatService {
  private static toReadMarkers = (
    markedAsRead: ReadMarkers | undefined
  ): Map<ChatterId, ConversationDataId> => {
    const readMarkers = new Map<ChatterId, ConversationDataId>();
    if (markedAsRead) {
      for (const [chatterId, conversationDataId] of Object.entries(
        markedAsRead
      )) {
        readMarkers.set(chatterId, conversationDataId);
      }
    }
    return readMarkers;
  };

  private static toConv = (conv: Partial<Conversation>): Conversation => {
    return {
      id: conv.id || "",
      creator: conv.creator || "",
      createdAt: conv.createdAt || 0,
      name: conv.name || "",
      data: conv.data || [],
      readMarkers: SseChatService.toReadMarkers(conv.readMarkers),
      dialogue: conv.dialogue || false,
      participants: conv.participants || [],
    };
  };
  private static toConvPart = (
    conv: Partial<Conversation>
  ): ConversationPart => {
    return {
      id: conv.id || "",
      data: conv.data || [],
      readMarkers: SseChatService.toReadMarkers(conv.readMarkers),
    };
  };

  static openSse(
    sseChatKey: string,
    jwt: string,
    onOpen: () => void,
    onReconnection: (e: { target: EventSource }) => void,
    onConnectionClosed: (e: { target: EventSource }) => void
  ): EventSource {
    const eventSource = new EventSourcePolyfill(
      config.chatApiSseBaseUrl() + "/v1/chat/api/sse",
      {
        headers: {
          "sse-chat-key": sseChatKey,
          Authorization: "Bearer " + jwt,
        },
      }
    );
    eventSource.onopen = (/*e: Event*/) => {
      console.log("sse-chat-service> SSE is OPEN");
      onOpen();
    };
    eventSource.onmessage = (e: MessageEvent) =>
      console.log("SSE message", JSON.parse(e.data));
    eventSource.onerror = (e: { target: EventSource }) => {
      console.log("sse-chat-service> SSE error", e);
      switch (e.target?.readyState) {
        case EventSource.CONNECTING:
          console.log("sse-chat-service> es.onerror", e);
          onReconnection(e);
          break;

        case EventSource.CLOSED:
          console.log(
            "sse-chat-service> Connection failed, will not reconnect"
          );
          onConnectionClosed(e);
          break;
      }
    };
    return eventSource;
  }

  static bind(
    eventSource: EventSource,
    userStore: UserStore,
    conversationsStore: ConversationsStore,
    chattersStore: ChattersStore
  ): void {
    // eventSource.addEventListener('PROFILE:INSTALL', (e: any) => {
    //     const parse = JSON.parse(e.data);
    //     domain.dispatch(ActionTypes.InstallProfile, parse.profile);
    // }, false);

    eventSource.addEventListener(
      "CHATTER:INSTALL",
      (e: any) => {
        const parse = JSON.parse(e.data);
        if (parse.chatter?.id == userStore.chatterId) {
          userStore.installProfile(parse.chatter);
        }
        chattersStore.installChatter(parse.chatter);
      },
      false
    );

    eventSource.addEventListener(
      "CONVERS:INSTALL",
      (e: any) => {
        const parse = JSON.parse(e.data);
        const conv: Partial<Conversation> = parse.conversation;
        conversationsStore.installConversation(SseChatService.toConv(conv));
      },
      false
    );

    eventSource.addEventListener(
      "CONVERS:PARTIAL",
      (e: any) => {
        const parse = JSON.parse(e.data);
        const conv: Partial<Conversation> = parse.conversation;
        conversationsStore.installConversationPart(
          SseChatService.toConvPart(conv)
        );
      },
      false
    );

    eventSource.addEventListener(
      "CONVERS:UPDATE:MESSAGE",
      (e: any) => {
        const parse = JSON.parse(e.data);
        conversationsStore.updateConversationAddMessage(
          parse.conversation.id,
          parse.conversation.data[0]
        );
      },
      false
    );

    eventSource.addEventListener(
      "CONVERS:UPDATE:READ_MARKER",
      (e: any) => {
        const parse = JSON.parse(e.data);
        conversationsStore.updateConversationMarkerAsRead(
          parse.conversation.id,
          new Map(Object.entries(parse.conversation.readMarkers))
        );
      },
      false
    );

    eventSource.addEventListener(
      "CONVERS:UPDATE:ADD_CHATTER",
      (e: any) => {
        const parse = JSON.parse(e.data);
        conversationsStore.updateConversationAddChatter(
          parse.conversation.id,
          parse.conversation.data[0]
        );
      },
      false
    );
  }
}
