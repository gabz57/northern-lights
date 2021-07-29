<template>
  <ul>
    <li v-for="(conversation) in conversations"
        :key="conversation.id"
        @click="$emit('selectConversation', conversation.id)">
      {{ "Conversation: " + conversation.id + " : " + conversation.name }}
      <ul>
        <li v-for="(event, eventIndex) in conversation.events" :key="eventIndex">
          {{ "Event: " + event.id + " - ..." }}
        </li>
      </ul>
    </li>
  </ul>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "ConversationList",
  props: {
    conversations: {
      type: Array,
      required: true
    },
  },
});

</script>

<style scoped lang="scss">
</style>
