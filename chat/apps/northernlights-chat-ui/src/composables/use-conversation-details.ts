import type { Ref } from "vue";
import { computed, reactive } from "vue";
import type {
  ChatterId,
  Conversation,
  ConversationDataId,
  ConversationId,
} from "@/domain/model";
import { useChattersStore } from "@/stores/chatter";
import { useUserStore } from "@/stores/user";
import { useConversationsStore } from "@/stores/conversations";

export type ConversationDetails = {
  id?: ConversationId;
  name?: string;
  dialogue?: boolean;
  createdBy?: ChatterId;
  createdAt?: number;
  nbUnreadMessages: number;
  participants: ChatterId[];
};

export const nbUnreadMsgs = (
  conversation: Conversation,
  chatterId: ChatterId
): number => {
  const lastConversationDataIdSeenByChatter: ConversationDataId | undefined =
    conversation.readMarkers[chatterId];
  const msgs = conversation.data;
  if (lastConversationDataIdSeenByChatter === undefined) {
    return msgs.length - (msgs[0]?.type === "CREATION" ? 1 : 0);
  }

  let unreadCount = 0;
  let i = msgs.length - 1;
  for (; i >= 0; i--) {
    if (msgs[i].id === lastConversationDataIdSeenByChatter) {
      break;
    }
    if (msgs[i].type !== "CREATION") {
      unreadCount++;
    }
  }
  return unreadCount;
};

export default function useConversationDetails(
  conversationIdRef: Ref<ConversationId>
): {
  details: ConversationDetails;
} {
  const userStore = useUserStore();
  const chattersStore = useChattersStore();
  const conversationsStore = useConversationsStore();

  const getOtherChatter = (conversation: Conversation) => {
    const selfChatterId = userStore.chatterId as NonNullable<
      typeof userStore.chatterId
    >;
    const otherChatterId = Array.from(conversation.participants.values()).find(
      (chatterId) => selfChatterId !== chatterId
    );
    let otherChatter;
    if (otherChatterId !== undefined) {
      otherChatter = chattersStore.getChatterById(otherChatterId);
    }
    return otherChatter;
  };

  const conversationRef = computed<Conversation>(() =>
    conversationsStore.getConversationById(conversationIdRef.value)
  );

  const conversationName = (conversation: Conversation): string => {
    return conversation.dialogue
      ? "1-1 with " + getOtherChatter(conversation)?.name || "WALL"
      : conversation.name;
  };

  return {
    details: reactive({
      id: computed(() => conversationRef.value.id),
      name: computed(() => conversationName(conversationRef.value)),
      dialogue: computed(() => conversationRef.value.dialogue),
      createdBy: computed(() => conversationRef.value.creator),
      createdAt: computed(() => conversationRef.value.createdAt),
      nbUnreadMessages: computed(() =>
        nbUnreadMsgs(conversationRef.value, userStore.chatterId)
      ),
      participants: computed(() => conversationRef.value?.participants || []),
    }),
  };
}
