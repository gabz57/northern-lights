<template>
  <div class="chat">
    <ChatHeader class="chat__header" />
    <div class="chat__separator" />
    <div class="chat__body">
      <ChatLeftMenu class="chat__body-menu" />
      <ChatContent class="chat__body-content" />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, watch } from "vue";
import type { Conversation, ConversationId } from "@/domain/model";
import ChatHeader from "@/components/chat/header/ChatHeader.vue";
import ChatLeftMenu from "@/components/chat/leftmenu/ChatLeftMenu.vue";
import ChatContent from "@/components/chat/ChatContent.vue";
import useSse from "@/composables/use-sse";
import { useUserStore } from "@/stores/user";
import { useUiStore } from "@/stores/ui";
import { useConversationsStore } from "@/stores/conversations";

export default defineComponent({
  name: "ChatApp",
  components: {
    ChatHeader,
    ChatContent,
    ChatLeftMenu,
  },
  setup() {
    useSse();
    const userStore = useUserStore();
    const uiStore = useUiStore();
    const conversationsStore = useConversationsStore();

    const selectConversation = (conversationId: ConversationId): void =>
      uiStore.setSelectedConversationId(conversationId);

    const autoSelectCreatedConversation = (
      newConversations: Conversation[],
      prevConversations: Conversation[]
    ) => {
      const createdConversation = newConversations.filter(
        (newConv) =>
          !prevConversations.some((prevConv) => prevConv.id === newConv.id)
      );
      if (createdConversation.length === 1) {
        // if chatter is creator => select created conversation
        if (createdConversation[0].creator === userStore.chatterId) {
          selectConversation(createdConversation[0].id);
        }
      }
    };

    watch(
      () => Array.from(conversationsStore.conversations.values()),
      autoSelectCreatedConversation
    );

    return {};
  },
});
</script>

<style scoped lang="scss">
@import "@/variables.scss";

.chat {
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: stretch;

  &__header {
    height: $header-height;
  }

  &__separator {
    margin: 0 10px;
    height: 1px;
    background-color: #8599ad;
  }

  &__body {
    flex-shrink: 0;
    flex-grow: 1;

    display: flex;
    flex-direction: row;
    width: 80%;
    margin: 0 auto;
    height: calc(100vh - #{$header-height});
    min-height: calc(100vh - #{$header-height});
    max-height: calc(100vh - #{$header-height});
  }
}
</style>
