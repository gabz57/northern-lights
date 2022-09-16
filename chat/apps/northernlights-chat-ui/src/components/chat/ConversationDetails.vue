<template>
  <div class="conversation-details">
    <h2>Info</h2>
    <div style="text-align: left">
      <div>
        <strong>Name: </strong>{{ details.name }}<br />
        <strong>Created: </strong><br />
        <strong>By: </strong>
        <ChatterLabel
          v-if="details.createdBy"
          :chatter-id="details.createdBy"
        />
        <br />
        <strong>At: </strong>{{ $filters.dateTime(details.createdAt * 1000) }}
      </div>
      <div>
        <strong>Members: </strong><br />
        <span v-for="chatterId in details.participants" :key="chatterId">
          - <ChatterLabel :chatter-id="chatterId" /><br />
        </span>
        <div v-if="!details.dialogue">
          <input
            placeholder="Invite chatter..."
            v-model="invitedChatterName"
            style="width: 100%"
          />
          <div v-for="chatter in invitableChatters" :key="chatter.id">
            <ChatterLabel :chatter-id="chatter.id" />
            <button @click="() => inviteChatter(chatter)">+</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import type { PropType } from "vue";
import { computed, defineComponent, ref, watch } from "vue";
import type { Chatter as ChatterState, ChatterId } from "@/domain/model";
import type { ConversationDetails } from "@/composables/use-conversation-details";
import ChatterLabel from "@/components/chat/ChatterLabel.vue";
import { useUserStore } from "@/stores/user";
import { useChattersStore } from "@/stores/chatter";
import { useConversationsStore } from "@/stores/conversations";

export default defineComponent({
  name: "ConversationDetails",
  components: { ChatterLabel },
  props: {
    details: {
      type: Object as PropType<ConversationDetails>,
      required: true,
    },
  },
  setup(props) {
    const userStore = useUserStore();
    const chattersStore = useChattersStore();
    const conversationsStore = useConversationsStore();

    const invitedChatterName = ref<string>("");
    const invitableChatters = ref<ChatterState[]>([]);

    const inviteChatter = (chatter: ChatterState) => {
      conversationsStore.inviteChatter(
        userStore.chatterId || "",
        props.details.id || "",
        chatter.id
      );
      invitedChatterName.value = "";
    };
    const invitedChatters = computed(() => props.details.participants);
    watch(
      [invitedChatterName, invitedChatters],
      ([chatterName]) => {
        const notCurrentChatter = (
          chatter: ChatterState,
          currentChatterId: ChatterId
        ) => chatter.id !== currentChatterId;
        const notAlreadyAdded = (
          chatter: ChatterState,
          chatterIds: ChatterId[]
        ) =>
          !chatterIds.find(
            (selectedChatterId) => chatter.id === selectedChatterId
          );

        const nextSelectableChatters = [];
        if (chatterName.length === 0) {
          invitableChatters.value = [];
        } else {
          // TODO: use chatter search
          for (const chatter of chattersStore.chatters.values()) {
            if (chatter.name.includes(chatterName)) {
              nextSelectableChatters.push(chatter);
            }
          }
          invitableChatters.value = nextSelectableChatters
            .filter((chatter) =>
              notCurrentChatter(chatter, userStore.chatterId || "")
            )
            .filter((chatter) =>
              notAlreadyAdded(chatter, props.details.participants)
            );
        }
      },
      {
        deep: true,
      }
    );

    return {
      invitedChatterName,
      invitableChatters,
      inviteChatter,
    };
  },
});
</script>

<style scoped lang="scss">
.conversation-details {
  padding: 10px;
}
</style>
