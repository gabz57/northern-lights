<template>
  <div class="chat-left-menu">
    <h1>Chat</h1>
    <h2>Chatters</h2>
    <ChatterList />
    <h2>Conversations</h2>
    <ConversationList />
    <button @click="toggleCreatingConversation">
      {{ creatingConversation ? "-" : "+" }}
    </button>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import ConversationList from "@/components/chat/leftmenu/ConversationList.vue";
import ChatterList from "@/components/chat/leftmenu/ChatterList.vue";
import useConversationCreator from "@/composables/use-conversation-creator";

export default defineComponent({
  name: "ChatLeftMenu",
  components: { ChatterList, ConversationList },
  setup() {
    return {
      ...useConversationCreator(),
    };
  },
});
</script>

<style scoped lang="scss">
@import "@/variables.scss";

.chat-left-menu {
  width: $chat-menu-width;
  flex-shrink: 0;
  flex-grow: 0;
  padding-right: 0.5rem;
}
</style>
