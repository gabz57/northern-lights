<template>
  <div class="chat-left-menu">
    <h1>Chat</h1>
    <h2>Chatters</h2>
    <ChatterList :chatter-ids="chatterIds"/>
    <h2>Conversations</h2>
    <ConversationList :conversation-ids="conversationIds" @selectConversation="selectConversation"/>
    <button @click="toggleCreatingConversation">{{ creatingConversation ? '-' : '+' }}</button>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import ConversationList from "@/components/chat/ConversationList.vue";
import ChatterList from "@/components/chat/ChatterList.vue";
import useConversationCreator from "@/composables/use-conversation-creator";
import useConversations from "@/composables/use-conversations";
import useChatters from "@/composables/use-chatters";

export default defineComponent({
  name: "ChatLeftMenu",
  components: {ChatterList, ConversationList},
  setup() {
    return {
      ...useChatters(),
      ...useConversations(false),
      ...useConversationCreator(),
    }
  }
})
</script>

<style scoped lang="scss">
@import "/src/variables.scss";

.chat-left-menu {
  width: $chat-menu-width;
  flex-shrink: 0;
  flex-grow: 0;
  padding-right: 0.5rem;
}
</style>
