import type { Ref } from "vue";
import { computed, reactive } from "vue";
import type {
  ChatterId,
  Conversation,
  ConversationDataId,
  ConversationId,
} from "@/domain/model";
import { useConversationsStore } from "@/stores/conversations";
import { useUserStore } from "@/stores/user";
import { nbUnreadMsgs } from "@/composables/use-conversation-details";

export type ChatterConversationDetails = {
  id?: ConversationId;
  // name?: string,
  createdBy?: ChatterId;
  createdAt?: number;
  nbUnreadMessages: number;
  participants: ChatterId[];
};

export default function useChatterConversationDetails(
  chatterIdRef: Ref<ChatterId>
): {
  details: ChatterConversationDetails;
} {
  const userStore = useUserStore();
  const conversationsStore = useConversationsStore();

  const conversationRef = computed(() =>
    conversationsStore.getPrivateConversationWithChatterId(chatterIdRef.value)
  );

  return {
    details: reactive({
      id: computed(() => conversationRef.value?.id),
      createdBy: computed(() => conversationRef.value?.creator),
      createdAt: computed(() => conversationRef.value?.createdAt),
      nbUnreadMessages: computed(() =>
        conversationRef.value
          ? nbUnreadMsgs(conversationRef.value, userStore.chatterId)
          : 0
      ),
      participants: computed(() => conversationRef.value?.participants || []),
    }),
  };
}
