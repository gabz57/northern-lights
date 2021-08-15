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
      <div class="chat__body-content" v-if="editingProfile">
        <!--        -->
      </div>
      <div class="chat__body-content" v-else-if="creatingConversation">
        <ConversationCreation />
      </div>
      <div class="chat__body-content" v-else-if="!creatingConversation && selectedConversationId">
        <Conversation :conversation-id="selectedConversationId"/>
      </div>
      <div class="chat__body-content" v-else>
        <div>Connect,<br/><br/>then<br/><br/>Select a conversation ... or create a new one</div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {useStore} from "@/store";
import {computed, defineComponent, ref, watch} from "vue";
import Conversation from "@/components/chat/Conversation.vue";
import ConversationList from "@/components/chat/ConversationList.vue";
import ChatterList from "@/components/chat/ChatterList.vue";
import {ConversationId} from "@/store/state";
import useSse from "@/composables/use-sse";
import ConversationCreation from "@/components/chat/ConversationCreation.vue";
import ChatHeader from "@/components/chat/ChatHeader.vue";

export default defineComponent({
  name: "Chat",
  components: {ChatHeader, ConversationCreation, ChatterList, ConversationList, Conversation},
  setup() {
    useSse()
    const store = useStore()

    const selectedConversationId = ref<ConversationId | undefined>(undefined)

    const chatters = computed(() => Array.from(store.state.chatters.values())
        .filter(chatter => chatter.id !== store.state.chatterId))
    const conversations = computed(() => Array.from(store.state.conversations.values()))
    const selectConversation = (conversationId: ConversationId): void => {
      selectedConversationId.value = conversationId
    }
    const editingProfile = ref<boolean>(false)
    const toggleEditingProfile = () => {
      editingProfile.value = !editingProfile.value
    }
    const creatingConversation = ref<boolean>(false)
    const toggleCreatingConversation = () => {
      creatingConversation.value = !creatingConversation.value
    }
    watch(conversations, (newConversations, prevConversations) => {
      const createdConversation = newConversations.filter(newConv => !prevConversations.some(prevConv => prevConv.id === newConv.id));
      if (createdConversation.length === 1) {
        if (createdConversation[0].creator === store.state.chatterId) {
          creatingConversation.value = false
          selectedConversationId.value = createdConversation[0].id
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
      toggleEditingProfile,
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
    flex-shrink: 0;
    flex-grow: 0;
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
      display: flex;
    }
  }
}
</style>
