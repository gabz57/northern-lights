<template>
  <div>
    <div >
      <ConversationMessage v-for="message in messages" :key="message.id" :message="message" />
    </div>
    <div>
      <input v-model="messageToSend"/>
      <button @click="sendMessage()">SEND</button>
    </div>
  </div>
</template>

<script lang="ts">
import useConversation from "@/composables/use-conversation";
import {defineComponent, ref, toRefs} from "vue";
import ConversationMessage from "@/components/chat/ConversationMessage.vue";

export default defineComponent({
  name: "Conversation",
  components: {ConversationMessage},
  props: {
    conversationId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const {conversationId} = toRefs(props)
    const {messages, sendMessage, markAsRead} = useConversation(conversationId)

    const messageToSend = ref("")

    return {
      messages,
      messageToSend,
      sendMessage: () => sendMessage(messageToSend.value),
      markAsRead
    }
  }

});

</script>

<style scoped lang="scss">
</style>
