<template>
  <div class="conversation-list">
    <ConversationListElement
      v-for="conversationId in conversationIds"
      :key="conversationId"
      :conversation-id="conversationId"
      @click="selectConversation(conversationId)"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import ConversationListElement from "@/components/chat/leftmenu/ConversationListElement.vue";
import useConversations from "@/composables/use-conversations";

export default defineComponent({
  name: "ConversationList",
  components: { ConversationListElement },
  setup() {
    return {
      ...useConversations(false),
    };
  },
});
</script>

<style scoped lang="scss">
.conversation-list {
  &:hover {
    cursor: pointer;
  }
}
</style>
