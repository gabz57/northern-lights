<template>
  <div class="chat">
    <div class="chat__header">
      <h1>Connexion</h1>
      <input v-model="userId" @keydown.enter="setChatterId()"/>
      <button @click="setChatterId()">CONNECT</button>
      <div>Sse AutoConnect : {{ sseState.sseAutoConnect }}</div>
      <div>Sse Wanted : {{ sseState.sseWanted }}</div>
      <div>Sse Open : {{ sseState.sseOpen }}</div>
    </div>
    <div class="chat__body">
      <div class="chat__body-menu">
        <h1>Chat</h1>
        <h2>Chatters</h2>
        <ChatterList :chatters="chatters"/>
        <h2>Conversations</h2>
        <ConversationList :conversations="conversations" @selectConversation="selectConversation"/>
      </div>
      <div class="chat__body-conversation" v-if="selectedConversationId">
        <h1>Conversation {{ selectedConversationId }}</h1>
        <Conversation :conversation-id="selectedConversationId"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {useStore} from "@/store";
import {computed, defineComponent, ref} from "vue";
import Conversation from "@/components/chat/Conversation.vue";
import ConversationList from "@/components/chat/ConversationList.vue";
import ChatterList from "@/components/chat/ChatterList.vue";
import {ConversationId} from "@/store/state";
import {ActionTypes} from "@/store/actions";
import useSse from "@/composables/use-sse";

export default defineComponent({
  name: "Chat",
  components: {ChatterList, ConversationList, Conversation},
  setup() {
    const {state} = useSse()
    const store = useStore()

    const userId = ref<string>("")
    const selectedConversationId = ref<ConversationId | undefined>(undefined)

    const chatters = computed(() => Array.from(store.state.chatters.values()))
    const conversations = computed(() => Array.from(store.state.conversations.values()))

    const selectConversation = (conversationId: ConversationId): void => {
      selectedConversationId.value = conversationId
    }
    const setChatterId = () => {
      selectedConversationId.value = undefined
      store.dispatch(ActionTypes.SetChatterId, {chatterId: userId.value});
    }
    return {
      sseState: state,
      userId,
      setChatterId,
      chatters,
      conversations,
      selectConversation,
      selectedConversationId,
    }
  }
})
</script>

<style scoped lang="scss">
.chat {
  display: flex;
  flex-direction: column;

  &__header {
    flex-shrink: 0;
    flex-grow: 0;
  }

  &__body {
    display: flex;
    flex-direction: row;
    flex-shrink: 0;
    flex-grow: 1;

    &-menu {
      width: 300px;
      flex-shrink: 0;
      flex-grow: 0;
    }

    &-conversation {
      flex-shrink: 0;
      flex-grow: 1;
    }
  }
}
</style>
