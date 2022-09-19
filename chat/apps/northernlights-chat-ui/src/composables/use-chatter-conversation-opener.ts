import type { Ref } from "vue";
import { computed } from "vue";
import type { ChatterId } from "@/domain/model";
import { useUserStore } from "@/stores/user";
import { useConversationsStore } from "@/stores/conversations";
import { useUiStore } from "@/stores/ui";

export default function useChatterConversationOpener(
  chatterIdRef: Ref<ChatterId>
): {
  openConversationWith: () => void;
} {
  const userStore = useUserStore();
  const conversationsStore = useConversationsStore();
  const uiStore = useUiStore();
  const conversationId = computed(() => {
    return Array.from(conversationsStore.conversations.values()).find((c) => {
      return (
        c.dialogue &&
        userStore.chatterId !== undefined &&
        c.participants.includes(userStore.chatterId) &&
        c.participants.includes(chatterIdRef.value)
      );
    })?.id;
  });

  const createDialogueConversation = (chatterId: ChatterId) => {
    if (chatterId === userStore.chatterId) {
      return;
    }
    conversationsStore.createConversation(
      userStore.chatterId || "",
      "",
      [userStore.chatterId, chatterId],
      true
    );
  };

  const openConversationWith = (): void => {
    if (chatterIdRef.value === userStore.chatterId) {
      return;
    }
    if (conversationId.value !== undefined) {
      uiStore.setSelectedConversationId(conversationId.value);
    } else {
      createDialogueConversation(chatterIdRef.value);
    }
  };

  return {
    openConversationWith,
  };
}
