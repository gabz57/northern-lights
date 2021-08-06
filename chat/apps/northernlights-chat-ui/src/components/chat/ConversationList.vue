<template>
  <ul>
    <li v-for="(conversationId) in conversationIds" :key="conversationId" style="text-align: left;">
      <ConversationListElement :conversation-id="conversationId"
                               @click="$emit('selectConversation', conversationId)" />
    </li>
  </ul>
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
    const conversationIds = computed(() => conversations.value.map(c => c.id))
    return {
      conversationIds: conversationIds,
    }
  }
});

</script>

<style scoped lang="scss">
</style>
