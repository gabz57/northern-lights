<template>
  <div class="conversation-list">
    <ConversationListElement
      v-for="conversationId in conversationIds"
      :key="conversationId"
      :conversation-id="conversationId"
      @click="$emit('selectConversation', conversationId)"
    />
  </div>
</template>

<script lang="ts">
import type { PropType } from "vue";
import { defineComponent } from "vue";
import ConversationListElement from "@/components/chat/ConversationListElement.vue";
import type { ConversationId } from "@/domain/model";

export default defineComponent({
  name: "ConversationList",
  components: { ConversationListElement },
  props: {
    conversationIds: {
      type: Object as PropType<ConversationId[]>,
      required: true,
    },
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
