<template>
  <div class="conversation">
    <div class="conversation__separator"/>
    <div class="conversation__messages">
      <h1 class="conversation__messages-header">{{ details.name || "&nbsp;" }}</h1>
      <ConversationMessages class="conversation__messages-messages" :messages="messages" :read-markers="readMarkers"/>
      <ConversationInput class="conversation__messages-input" @sendMessage="sendMessage"/>
    </div>
    <div class="conversation__separator"/>
    <ConversationDetails class="conversation__details" :details="details"/>
  </div>
</template>

<script lang="ts">
import useConversation from "@/composables/use-conversation";
import {defineComponent, toRef} from "vue";
import ConversationMessages from "@/components/chat/ConversationMessages.vue";
import ConversationDetails from "@/components/chat/ConversationDetails.vue";
import ConversationInput from "@/components/chat/ConversationInput.vue";

export default defineComponent({
  name: "ConversationWrapper",
  components: {ConversationMessages, ConversationInput, ConversationDetails},
  props: {
    conversationId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    return {
      ...useConversation(toRef(props, 'conversationId'))
    }
  }

});
</script>

<style scoped lang="scss">
@import "src/variables";
.conversation {
  display: flex;
  flex-direction: row;
  flex-grow: 1;
  height: inherit;
  min-height: inherit;
  max-height: inherit;

  &__separator {
    margin: 8rem 0 6rem;
    width: 1px;
    background-color: #8599ad;
  }

  &__messages {
    flex-shrink: 0;
    flex-grow: 1;

    display: flex;
    flex-direction: column;
    min-height: calc(100vh - #{$header-height} - #{$conversation-input-height} - #{$conversation-messages-header-height});
    max-height: calc(100vh - #{$header-height} - #{$conversation-input-height} - #{$conversation-messages-header-height});

    &-header {
      height: $conversation-messages-header-height;
      line-height: $conversation-messages-header-height;
      margin: 0;
    }

    &-messages {
      flex-grow: 1;
      flex-shrink: 1;
    }

    &-input {
      flex-grow: 0;
      flex-shrink: 0;
    }
  }

  &__details {
    width: 220px;
    flex-shrink: 0;
    flex-grow: 0;
  }
}
</style>
