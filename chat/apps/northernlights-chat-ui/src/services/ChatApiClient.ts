import type { ConversationDataId, ConversationId } from "@/domain/model";
import { config } from "@/services/EnvConfig";

const debounceFetch = <
  Params extends [string, ConversationId, ConversationDataId]
>(
  func: (...args: Params) => Promise<unknown>,
  timeout = 300
): ((...args: Params) => void) => {
  let timer: ReturnType<typeof setTimeout>;
  const prevDataIdByConvId: { [k: ConversationId]: ConversationDataId } = {};
  return (...args: Params) => {
    const later = () => {
      clearTimeout(timeout);
      return func(...args);
    };

    if (
      prevDataIdByConvId[args[1]] === undefined ||
      Number(args[2]) > Number(prevDataIdByConvId[args[1]])
    ) {
      clearTimeout(timer);
      prevDataIdByConvId[args[1]] = args[2];
      timer = setTimeout(later, timeout);
    }
  };
};

const debouncedMarkedAsRead = debounceFetch(
  (
    jwt: string,
    cId: ConversationId,
    cDataId: ConversationDataId
  ): Promise<Response> => {
    return ChatApiClient.post(jwt, "mark-as-read", {
      conversationId: cId,
      conversationDataId: cDataId,
    });
  },
  200
);
class ChatApiClient {
  private lastEmittedConversationDataIdPerConversationId = new Map<
    string,
    string
  >();
  private jwt = "";

  setJwt(jwt: string) {
    this.jwt = jwt;
  }

  async initSse(
    conversationStatuses: Map<ConversationId, ConversationDataId>
  ): Promise<string> {
    this.lastEmittedConversationDataIdPerConversationId.clear();
    return (
      await (
        await ChatApiClient.post(this.jwt, "init-sse", {
          conversationStatuses: Object.fromEntries(conversationStatuses),
        })
      ).json()
    ).sseChatKey;
  }

  async createConversation(
    userId: string,
    name: string,
    participants: string[],
    dialogue: boolean
  ): Promise<void> {
    await ChatApiClient.post(this.jwt, "open", {
      name,
      participants,
      dialogue,
    });
  }

  async sendMessage(
    userId: string,
    conversationId: string,
    message: string
  ): Promise<void> {
    await ChatApiClient.post(this.jwt, "message", {
      conversationId,
      message,
    });
  }

  async markAsRead(
    userId: string,
    conversationId: string,
    conversationDataId: string
  ): Promise<void> {
    const lastEmittedConversationDataId =
      this.lastEmittedConversationDataIdPerConversationId.get(conversationId);
    if (
      lastEmittedConversationDataId === undefined ||
      lastEmittedConversationDataId < conversationDataId
    ) {
      this.lastEmittedConversationDataIdPerConversationId.set(
        conversationId,
        conversationDataId
      );
    }
    debouncedMarkedAsRead(this.jwt, conversationId, conversationDataId);
  }

  async inviteChatter(
    userId: string,
    conversationId: string,
    invitedChatterId: string
  ): Promise<void> {
    await ChatApiClient.post(this.jwt, "invite-chatter", {
      conversationId,
      chatterId: invitedChatterId,
    });
  }

  static async post(
    jwt: string,
    endpoint: string,
    payload: unknown
  ): Promise<Response> {
    return fetch(config.chatApiBaseUrl() + "/v1/chat/api/" + endpoint, {
      method: "POST",
      body: JSON.stringify(payload), // string or object
      headers: new Headers({
        Authorization: "Bearer " + jwt,
        Accept: "application/json",
        "Content-Type": "application/json",
      }),
    });
  }
}

const chatApiClient = new ChatApiClient();

export { chatApiClient };
