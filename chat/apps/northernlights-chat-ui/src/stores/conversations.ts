import { defineStore } from "pinia";
import { computed } from "vue";
import type {
  ChatterId,
  Conversation,
  ConversationChatterData,
  ConversationData,
  ConversationDataId,
  ConversationId,
  ConversationMessageData,
  ConversationPart,
  Conversations,
  ReadMarkers,
} from "@/domain/model";
import { useUserStore } from "@/stores/user";
import { chatApiClient } from "@/services/ChatApiClient";
import { useLocalStorage, useSessionStorage } from "@vueuse/core";

export type ConversationsStore = ReturnType<typeof useConversationsStore>;

export const useConversationsStore = defineStore("conversations", () => {
  const userStore = useUserStore();

  // state
  const conversations = useLocalStorage<Conversations>(
    "conversations",
    new Map<ConversationId, Conversation>(),
    {
      serializer: {
        read: (v: string): Conversations => {
          return v
            ? new Map<ConversationId, Conversation>(JSON.parse(v))
            : new Map<ConversationId, Conversation>();
        },
        write: (v: Conversations): string => {
          return JSON.stringify(Array.from(v.entries()));
        },
      },
    }
  );

  const lastReadMarkerEmitted = useSessionStorage<
    Map<ConversationId, ConversationDataId>
  >("emittedReadMarkers", new Map<ConversationId, ConversationDataId>(), {
    serializer: {
      read: (v: string): Map<ConversationId, ConversationDataId> => {
        return v
          ? new Map<ConversationId, ConversationDataId>(JSON.parse(v))
          : new Map<ConversationId, ConversationDataId>();
      },
      write: (v: Map<ConversationId, ConversationDataId>): string => {
        return JSON.stringify(Array.from(v.entries()));
      },
    },
  });

  // getters
  const getConversationById = computed(() => (id: ConversationId) => {
    const conversation: Conversation | undefined = conversations.value.get(id);
    if (conversation == undefined) {
      throw new Error("Using unknown conversation id : " + id);
    }
    return conversation;
  });
  const getPrivateConversationWithChatterId = computed(
    () => (chatterId: ChatterId) => {
      return Array.from(conversations.value.values()).find(
        (c) =>
          c.dialogue &&
          userStore.chatterId !== undefined &&
          c.participants.includes(userStore.chatterId) &&
          c.participants.includes(chatterId)
      );
    }
  );

  // actions
  function installConversation(conversation: Conversation) {
    conversations.value.set(conversation.id, conversation);
  }

  function installConversationPart(conversationPart: ConversationPart) {
    const conversation = conversations.value.get(conversationPart.id);
    if (conversation === undefined) return;
    conversationPart.data.forEach((conversationData: ConversationData) =>
      conversation.data.push(conversationData)
    );

    conversation.readMarkers = {
      ...conversation.readMarkers,
      ...conversationPart.readMarkers,
    };
  }

  function updateConversationAddMessage(
    conversationId: ConversationId,
    conversationMessageData: ConversationMessageData
  ) {
    conversations.value.get(conversationId)?.data.push(conversationMessageData);
  }

  function updateConversationAddChatter(
    conversationId: ConversationId,
    conversationChatterData: ConversationChatterData
  ) {
    conversations.value
      .get(conversationId)
      ?.participants.push(conversationChatterData.chatterId);
    conversations.value.get(conversationId)?.data.push(conversationChatterData);
  }

  function updateConversationMarkerAsRead(
    conversationId: ConversationId,
    readMarkers: ReadMarkers
  ) {
    const conversation = conversations.value.get(conversationId);
    if (conversation === undefined) return;

    conversation.readMarkers = {
      ...conversation.readMarkers,
      ...readMarkers,
    };
  }

  async function sendMessage(
    chatterId: ChatterId,
    conversationId: ConversationId,
    message: string
  ) {
    await chatApiClient.sendMessage(chatterId, conversationId, message);
  }

  async function markAsRead(
    chatterId: ChatterId,
    conversationId: ConversationId,
    conversationDataId: ConversationDataId
  ) {
    if (
      lastReadMarkerEmitted.value.get(conversationId) === undefined ||
      Number(lastReadMarkerEmitted.value.get(conversationId)) <
        Number(conversationDataId)
    ) {
      lastReadMarkerEmitted.value.set(conversationId, conversationDataId);
      await chatApiClient.markAsRead(
        chatterId,
        conversationId,
        conversationDataId
      );
    }
  }

  async function createConversation(
    chatterId: ChatterId,
    name: string,
    participants: ChatterId[],
    dialogue: boolean
  ) {
    await chatApiClient.createConversation(
      chatterId,
      name,
      participants,
      dialogue
    );
  }

  async function inviteChatter(
    chatterId: ChatterId,
    conversationId: ConversationId,
    invitedChatterId: ChatterId
  ) {
    await chatApiClient.inviteChatter(
      chatterId,
      conversationId,
      invitedChatterId
    );
  }

  return {
    conversations,
    getConversationById,
    getPrivateConversationWithChatterId,
    lastReadMarkerEmitted,
    installConversation,
    installConversationPart,
    updateConversationAddMessage,
    updateConversationAddChatter,
    updateConversationMarkerAsRead,
    sendMessage,
    markAsRead,
    createConversation,
    inviteChatter,
  };
});
