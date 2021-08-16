<template>
  <div class="chatter-list-element"
       :class="{'chatter-list-element--with-new-message': details.nbUnreadMessages > 0}"
       @click="() => openDirectConversation(chatterId)">
    <Chatter :chatter-id="chatterId" />
    <span>
      {{ details.nbUnreadMessages }}
    </span>
  </div>
</template>

<script lang="ts">
/* eslint-disable no-debugger */

import {defineComponent, toRefs} from "vue";
import Chatter from "@/components/chat/Chatter.vue";
import {ChatterId} from "@/store/state";
import {ActionTypes} from "@/store/actions";
import {useStore} from "@/store";
import useChatterConversationDetails from "@/composables/use-chatter-conversation-details";

export default defineComponent({
  name: "ChatterListElement",
  components: {Chatter},
  props: {
    chatterId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const {chatterId: chatterIdRef} = toRefs(props)

    const store = useStore();
    const {details} = useChatterConversationDetails(chatterIdRef)
    const createDialogueConversation = (chatterId: ChatterId) => {
      store.dispatch(ActionTypes.CreateConversation, {
        chatterId: store.state.chatterId || "",
        name: "",
        participants: [chatterId],
        dialogue: true,
      })
    }

    const openDirectConversation = (chatterId: ChatterId): void => {
      console.log("chatterId", chatterId)
      const directConversation = Array.from(store.state.conversations.values()).find(c => {
        return c.dialogue
            && store.state.chatterId !== undefined && c.participants.includes(store.state.chatterId)
            && c.participants.includes(chatterId)
      })
      if (directConversation !== undefined) {
        store.dispatch(ActionTypes.SetSelectedConversationId, directConversation.id)
      } else {
        createDialogueConversation(chatterId)
      }
    }

    return {
      openDirectConversation,
      details,
    }
  }
});

</script>

<style scoped lang="scss">
.chatter-list-element {
  display: flex;
  justify-content: space-between;
  padding: 0.4rem 1rem;

  &--with-new-message {
    font-weight: bold;
  }

  &:hover {
    cursor: pointer;
    border-radius: 2rem;
    background-color: lightblue;
  }
}
</style>
