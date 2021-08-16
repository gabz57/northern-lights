<template>
  <div class="conversation-list">
    <ConversationListElement v-for="(conversationId) in conversationIds" :key="conversationId"
                             :conversation-id="conversationId"
                             @click="$emit('selectConversation', conversationId)"/>
  </div>
</template>

<script lang="ts">
import {computed, defineComponent, PropType, toRefs} from "vue";
import ConversationListElement from "@/components/chat/ConversationListElement.vue";
import {Conversation} from "@/store/state";

export default defineComponent({
  name: "ConversationList",
  components: {ConversationListElement},
  props: {
    conversations: {
      type: Object as PropType<Conversation[]>,
      required: true
    },
  },
  setup(props) {
    const {conversations} = toRefs(props)
    const conversationIds = computed(() => conversations.value
        .filter(conversation => conversation.dialogue === false)
        .map(c => c.id))
    return {
      conversationIds: conversationIds,
    }
  }
});

</script>

<style scoped lang="scss">
.conversation-list {
  &:hover {
    cursor: pointer;
  }
}
</style>
