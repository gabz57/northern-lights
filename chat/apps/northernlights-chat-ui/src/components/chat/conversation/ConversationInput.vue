<template>
  <div class="conversation-input">
    <input v-model="messageToSend" @keydown.enter="sendMessage" />
    <button @click="sendMessage">SEND</button>
  </div>
</template>
<script lang="ts">
import { ref } from "vue";

export default {
  name: "ConversationInput",
  emits: ["submit"],
  setup(props: any, { emit }: any) {
    const messageToSend = ref("");
    const sendMessage = () => {
      emit("submit", messageToSend.value);
      messageToSend.value = "";
    };
    return {
      sendMessage,
      messageToSend,
    };
  },
};
</script>

<style scoped lang="scss">
@import "@/variables.scss";

.conversation-input {
  display: flex;
  height: $conversation-input-height;
  padding: 1rem;

  input {
    flex-grow: 1;
  }

  button {
    flex-grow: 0;
  }
}
</style>
