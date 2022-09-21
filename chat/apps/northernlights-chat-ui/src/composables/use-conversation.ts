import type { Ref } from "vue";
import { computed, onMounted, ref, watch } from "vue";
import type {
  ChatterId,
  Conversation,
  ConversationData,
  ConversationDataId,
  ConversationId,
  ReadMarkers,
} from "@/domain/model";
import type { ConversationDetails } from "@/composables/use-conversation-details";
import useConversationDetails from "@/composables/use-conversation-details";
import { useConversationsStore } from "@/stores/conversations";
import { useUserStore } from "@/stores/user";
import { useUiStore } from "@/stores/ui";

type ReadMarkersReversed = Map<ConversationDataId, ChatterId[]>;

export type Markable = {
  index: number;
  readBy: ChatterId[];
  onVisible: () => void;
  watchVisible: boolean;
};

export type MarkableConversationData = ConversationData & Markable;

const reverse = (readMarkers: ReadMarkers): ReadMarkersReversed => {
  const reversed = new Map<ConversationDataId, ChatterId[]>();
  for (const [chatterId, conversationDataId] of Object.entries(readMarkers)) {
    const chatterIds = reversed.get(conversationDataId) || [];
    chatterIds.push(chatterId);
    reversed.set(conversationDataId, chatterIds);
  }
  return reversed;
};

export default function useConversation(
  conversationIdRef: Ref<ConversationId>
): {
  details: ConversationDetails;
  sendMessage: (message: string) => void;
  markAsRead: (conversationDataId: ConversationDataId) => void;
  content: Ref<MarkableConversationData[]>;
  readMarkers: Ref<ReadMarkers>;
} {
  const conversationsStore = useConversationsStore();
  const userStore = useUserStore();
  const uiStore = useUiStore();

  const sendMessage = (message: string) => {
    conversationsStore.sendMessage(
      userStore.chatterId || "",
      conversationIdRef.value,
      message
    );
  };
  const markAsRead = (conversationDataId: ConversationDataId) => {
    if (uiStore.visible) {
      conversationsStore.markAsRead(
        userStore.chatterId || "",
        conversationIdRef.value,
        conversationDataId
      );
    }
  };
  const content = ref<MarkableConversationData[]>([]);
  const readMarkers = ref<ReadMarkers>({});

  const conversationRef = computed<Conversation>(() =>
    conversationsStore.getConversationById(conversationIdRef.value)
  );
  onMounted(() => loadConversationContent(conversationRef.value));
  watch(
    conversationRef,
    (conversation) => loadConversationContent(conversation),
    { deep: true }
  );
  const loadConversationContent = (conversation: Conversation) => {
    content.value = withReadMarkers(
      conversation.data,
      conversation.readMarkers
    );
    readMarkers.value = conversation.readMarkers;
  };

  const withReadMarkers = (
    data: ConversationData[],
    markers: ReadMarkers
  ): MarkableConversationData[] => {
    const reversed: ReadMarkersReversed = reverse(markers);
    const marker: ConversationDataId | undefined =
      markers[userStore.chatterId];
    let index = 0;
    return data.map((conversationData) => {
      const watchVisible =
        !marker || Number(conversationData.id) > Number(marker);
      return {
        ...conversationData,
        readBy: reversed.get(conversationData.id) || [],
        watchVisible,
        onVisible: () => {
          watchVisible && markAsRead(conversationData.id);
        },
        index: index++,
      };
    });
  };

  return {
    ...useConversationDetails(conversationIdRef),
    sendMessage,
    markAsRead,
    content,
    readMarkers,
  };
}
