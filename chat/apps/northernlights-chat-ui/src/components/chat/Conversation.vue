<template>
  <div class="conversation">
    <div class="conversation__messages">
      <ConversationMessages :messages="messages"/>
      <div>
        <input v-model="messageToSend" @keydown.enter="sendMessage()"/>
        <button @click="sendMessage()">SEND</button>
      </div>
    </div>
    <div class="conversation__details">
      <ConversationDetails :details="details" />
    </div>
  </div>
</template>

<script lang="ts">
import useConversation from "@/composables/use-conversation";
import {defineComponent, ref, toRefs} from "vue";
import ConversationMessages from "@/components/chat/ConversationMessages.vue";
import ConversationDetails from "@/components/chat/ConversationDetails.vue";
import {ConversationDataId} from "@/store/state";

export default defineComponent({
  name: "Conversation",
  components: {ConversationDetails, ConversationMessages},
  props: {
    conversationId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const {conversationId} = toRefs(props)
    const {details, messages, sendMessage, markAsRead} = useConversation(conversationId)

    const messageToSend = ref("")

    return {
      details,
      messages,
      messageToSend,
      sendMessage: () => {
        sendMessage(messageToSend.value);
        messageToSend.value = ""
      },
      markAsRead: (conversationDataId: ConversationDataId) => {
        markAsRead(conversationDataId)
      }
    }
  }

});

</script>

<style scoped lang="scss">
.conversation {
  display: flex;
  flex-direction: row;

  &__messages {
    flex-shrink: 0;
    flex-grow: 1;
  }

  &__details {
    width: 200px;
    flex-shrink: 0;
    flex-grow: 0;
  }
}
</style>
