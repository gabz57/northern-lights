<template>
  <div class="chat-content">
    <ChatterProfile v-if="editingProfile" />
    <ConversationCreation v-else-if="creatingConversation" />
    <ConversationWrapper
      v-else-if="selectedConversationId"
      :conversation-id="selectedConversationId"
    />
    <div v-else style="display: flex; height: 100%">
      <div style="margin: auto">
        Connect,<br /><br />then<br /><br />Select a conversation ... or create
        a new one
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from "vue";
import ConversationWrapper from "@/components/chat/conversation/Conversation.vue";
import ConversationCreation from "@/components/chat/conversation/ConversationCreation.vue";
import ChatterProfile from "@/components/chat/ChatterProfile.vue";
import { useUiStore } from "@/stores/ui";

export default defineComponent({
  name: "ChatContent",
  components: {
    ChatterProfile,
    ConversationCreation,
    ConversationWrapper,
  },
  setup() {
    const uiStore = useUiStore();

    return {
      selectedConversationId: computed(() => uiStore.selectedConversationId),
      creatingConversation: computed(() => uiStore.creatingConversation),
      editingProfile: computed(() => uiStore.editingProfile),
    };
  },
});
</script>

<style scoped lang="scss">
@import "@/variables.scss";

.chat-content {
  flex-shrink: 0;
  flex-grow: 1;
  min-height: inherit;
  max-height: inherit;
}
</style>
