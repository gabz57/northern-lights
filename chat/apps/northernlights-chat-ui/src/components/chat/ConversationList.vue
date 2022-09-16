<template>
  <div class="conversation-list">
    <ConversationListElement v-for="(conversationId) in conversationIds" :key="conversationId"
                             :conversation-id="conversationId"
                             @click="$emit('selectConversation', conversationId)"/>
  </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue";
import ConversationListElement from "@/components/chat/ConversationListElement.vue";
import {ConversationId} from "@/domain/model";

export default defineComponent({
  name: "ConversationList",
  components: {ConversationListElement},
  props: {
    conversationIds: {
      type: Object as PropType<ConversationId[]>,
      required: true
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
