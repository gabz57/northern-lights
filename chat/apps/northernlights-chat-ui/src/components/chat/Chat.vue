<template>
  <div class="chat">
    <ChatHeader class="chat__header"/>
    <div class="chat__separator"/>
    <div class="chat__body">
      <ChatLeftMenu class="chat__body-menu"/>
      <ChatContent class="chat__body-content"/>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, watch} from "vue";
import {useStore} from "@/store";
import {Conversation, ConversationId} from "@/store/state";
import {ActionTypes} from "@/store/actions";
import ChatHeader from "@/components/chat/ChatHeader.vue";
import ChatLeftMenu from "@/components/chat/ChatLeftMenu.vue";
import ChatContent from "@/components/chat/ChatContent.vue";
import useSse from "@/composables/use-sse";

export default defineComponent({
  name: "Chat",
  components: {
    ChatHeader,
    ChatContent,
    ChatLeftMenu,
  },
  setup() {
    useSse()
    const store = useStore()

    const selectConversation = (conversationId: ConversationId): void => {
      store.dispatch(ActionTypes.SetSelectedConversationId, conversationId)
    }

    const autoSelectCreatedConversation = (newConversations: Conversation[], prevConversations: Conversation[]) => {
      const createdConversation = newConversations.filter(newConv => !prevConversations.some(prevConv => prevConv.id === newConv.id));
      if (createdConversation.length === 1) {
        // if chatter is creator => select created conversation
        if (createdConversation[0].creator === store.state.chatterId) {
          selectConversation(createdConversation[0].id)
        }
      }
    };

    watch(() => Array.from(store.state.conversations.values()), autoSelectCreatedConversation)

    return {}
  }
})
</script>

<style scoped lang="scss">
@import "/src/variables.scss";

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
