<template>
  <div class="conversation-details">
    <h2>Info</h2>
    <div style="text-align: left">
      <div>
        <strong>Name: </strong>{{ details.name }}<br/>
        <strong>Created: </strong><br/>
        <strong>By: </strong>
        <ChatterLabel v-if="details.createdBy" :chatter-id="details.createdBy"/>
        <br/>
        <strong>At: </strong>{{ $filters.dateTime(details.createdAt * 1000) }}
      </div>
      <div>
        <strong>Members: </strong><br/>
        <span v-for="chatterId in details.participants" :key="chatterId">
          - <ChatterLabel :chatter-id="chatterId"/><br/>
        </span>
        <div v-if="!details.dialogue">
          <input placeholder="Invite chatter..." v-model="invitedChatterName" style="width: 100%">
          <div v-for="chatter in invitableChatters" :key="chatter.id">
            <ChatterLabel :chatter-id="chatter.id"/>
            <button @click="() => inviteChatter(chatter)">+</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {computed, defineComponent, PropType, ref, watch} from "vue";
import {useStore} from "@/store";
import {Chatter as ChatterState, ChatterId} from "@/store/state";
import {ActionTypes} from "@/store/actions";
import {ConversationDetails} from "@/composables/use-conversation-details";
import ChatterLabel from "@/components/chat/ChatterLabel.vue";

export default defineComponent({
  name: "ConversationDetails",
  components: {ChatterLabel},
  props: {
    details: {
      type: Object as PropType<ConversationDetails>,
      required: true
    },
  },
  setup(props) {
    const store = useStore();

    const invitedChatterName = ref<string>("")
    const invitableChatters = ref<ChatterState[]>([])

    const inviteChatter = (chatter: ChatterState) => {
      store.dispatch(ActionTypes.InviteChatter, {
        chatterId: store.state.chatterId || "",
        conversationId: props.details.id || "",
        invitedChatterId: chatter.id
      })
      invitedChatterName.value = ""
    }
    const invitedChatters = computed(() => props.details.participants)
    watch([invitedChatterName, invitedChatters], ([chatterName]) => {
      const notCurrentChatter = (chatter: ChatterState, currentChatterId: ChatterId) => chatter.id !== currentChatterId;
      const notAlreadyAdded = (chatter: ChatterState, chatterIds: ChatterId[]) => !chatterIds.find(selectedChatterId => chatter.id === selectedChatterId);

      const nextSelectableChatters = []
      if (chatterName.length === 0) {
        invitableChatters.value = []
      } else {
        // TODO: use chatter search
        for (let chatter of store.state.chatters.values()) {
          if (chatter.name.includes(chatterName)) {
            nextSelectableChatters.push(chatter)
          }
        }
        invitableChatters.value = nextSelectableChatters
            .filter(chatter => notCurrentChatter(chatter, store.state.chatterId || ""))
            .filter(chatter => notAlreadyAdded(chatter, props.details.participants))
      }
    }, {
      deep: true
    })

    return {
      invitedChatterName,
      invitableChatters,
      inviteChatter,
    }
  }
});

</script>

<style scoped lang="scss">
.conversation-details {
  padding: 10px;
}
</style>
