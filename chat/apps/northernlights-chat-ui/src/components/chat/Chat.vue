<template>
  <div class="chat">
    <ChatHeader class="chat__header" />
    <div class="chat__separator"/>
    <div class="chat__body">
      <div class="chat__body-menu">
        <h1>Chat</h1>
        <h2>Chatters</h2>
        <ChatterList :chatters="chatters"/>
        <h2>Conversations</h2>
        <ConversationList :conversations="conversations" @selectConversation="selectConversation"/>
        <button @click="toggleCreatingConversation">{{ creatingConversation ? '-' : '+' }}</button>
      </div>
      <div class="chat__body-content">
        <ChatterProfile v-if="editingProfile" />
        <ConversationCreation v-else-if="creatingConversation" />
        <Conversation v-else-if="!creatingConversation && selectedConversationId" :conversation-id="selectedConversationId"/>
        <div v-else style="display: flex; height: 100%">
          <div style="margin: auto">Connect,<br/><br/>then<br/><br/>Select a conversation ... or create a new one</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {useStore} from "@/store";
import {computed, defineComponent, watch} from "vue";
import Conversation from "@/components/chat/Conversation.vue";
import ConversationList from "@/components/chat/ConversationList.vue";
import ChatterList from "@/components/chat/ChatterList.vue";
import {ConversationId} from "@/store/state";
import useSse from "@/composables/use-sse";
import ConversationCreation from "@/components/chat/ConversationCreation.vue";
import ChatHeader from "@/components/chat/ChatHeader.vue";
import ChatterProfile from "@/components/chat/ChatterProfile.vue";
import {ActionTypes} from "@/store/actions";

export default defineComponent({
  name: "Chat",
  components: {ChatterProfile, ChatHeader, ConversationCreation, ChatterList, ConversationList, Conversation},
  setup() {
    useSse()
    const store = useStore()

    const selectedConversationId = computed(() => store.state.ui.selectedConversationId)

    const chatters = computed(() => Array.from(store.state.chatters.values())
        .filter(chatter => chatter.id !== store.state.chatterId))
    const conversations = computed(() => Array.from(store.state.conversations.values()))
    const selectConversation = (conversationId: ConversationId): void => {
      store.dispatch(ActionTypes.SetSelectedConversationId, conversationId)
    }
    const editingProfile = computed(() => store.state.ui.editingProfile)
    const creatingConversation = computed(() => store.state.ui.creatingConversation)
    const toggleCreatingConversation = () => {
      store.dispatch(ActionTypes.SetCreatingConversation, !creatingConversation.value)
    }

    watch(conversations, (newConversations, prevConversations) => {
      const createdConversation = newConversations.filter(newConv => !prevConversations.some(prevConv => prevConv.id === newConv.id));
      if (createdConversation.length === 1) {
        if (createdConversation[0].creator === store.state.chatterId) {
          store.dispatch(ActionTypes.SetCreatingConversation, false)
          selectConversation(createdConversation[0].id)
        }
      }
    })

    return {
      chatters,
      conversations,
      selectConversation,
      selectedConversationId,
      toggleCreatingConversation,
      creatingConversation,
      editingProfile
    }
  }
})
</script>

<style scoped lang="scss">
@import "/src/variables.scss";

.chat {
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: stretch;
  &__header {
    height: $header-height;
  }
  &__separator {
    margin: 0 10px;
    height: 1px;
    background-color: #8599ad;
  }
  &__body {
    flex-shrink: 0;
    flex-grow: 1;

    display: flex;
    flex-direction: row;
    width: 80%;
    margin: 0 auto;
    height: calc(100vh - #{$header-height});
    min-height: calc(100vh - #{$header-height});
    max-height: calc(100vh - #{$header-height});

    &-menu {
      width: $chat-menu-width;
      flex-shrink: 0;
      flex-grow: 0;
    }

    &-content {
      flex-shrink: 0;
      flex-grow: 1;
      min-height: inherit;
      max-height: inherit;
    }
  }
}
</style>
