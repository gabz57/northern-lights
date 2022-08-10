<template>
  <div class="chat-content">
    <ChatterProfile v-if="editingProfile"/>
    <ConversationCreation v-else-if="creatingConversation"/>
    <ConversationWrapper v-else-if="selectedConversationId" :conversation-id="selectedConversationId"/>
    <div v-else style="display: flex; height: 100%">
      <div style="margin: auto">Connect,<br/><br/>then<br/><br/>Select a conversation ... or create a new one</div>
    </div>
  </div>
</template>

<script lang="ts">
import {useStore} from "@/store";
import {computed, defineComponent} from "vue";
import ConversationWrapper from "@/components/chat/Conversation.vue";
import ConversationCreation from "@/components/chat/ConversationCreation.vue";
import ChatterProfile from "@/components/chat/ChatterProfile.vue";

export default defineComponent({
  name: "ChatContent",
  components: {
    ChatterProfile, ConversationCreation, ConversationWrapper
  },
  setup() {
    const store = useStore()

    return {
      selectedConversationId: computed(() => store.state.ui.selectedConversationId),
      creatingConversation: computed(() => store.state.ui.creatingConversation),
      editingProfile: computed(() => store.state.ui.editingProfile)
    }
  }
})
</script>

<style scoped lang="scss">
@import "/src/variables.scss";

.chat-content {
  flex-shrink: 0;
  flex-grow: 1;
  min-height: inherit;
  max-height: inherit;
}
</style>
