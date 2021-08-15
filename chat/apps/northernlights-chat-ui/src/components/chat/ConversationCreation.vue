<template>
  <div class="conversation-creation">

    Name: <input v-model="newConversationName">
    <button @click="createConversation" :disabled="newConversationName.length === 0 || selectedChatters.length === 0">CREATE</button>
    <div>
      <div>Chatters:</div>
      <ul>
        <li v-for="chatter in selectedChatters" :key="chatter.id">{{ chatter.name }}</li>
      </ul>
    </div>
    <div>Add chatters :</div>
    <div>
      <input v-model="newChatterName">
      <div v-for="chatter in selectableChatters" :key="chatter.id">
        {{ chatter.name }}
        <button @click="() => addChatter(chatter)">+</button>
      </div>
    </div>

  </div>
</template>

<script lang="ts">
/* eslint-disable no-debugger */

import {defineComponent, ref, watch} from "vue";
import {Chatter, ChatterId} from "@/store/state";
import {useStore} from "@/store";
import {ActionTypes} from "@/store/actions";

export default defineComponent({
  name: "ConversationCreation",
  setup() {
    const store = useStore();
    const selectedChatters = ref<Chatter[]>([])
    const selectableChatters = ref<Chatter[]>([])
    const newConversationName = ref<string>("")
    const newChatterName = ref<string>("")
    const createConversation = () => {
      store.dispatch(ActionTypes.CreateConversation, {
        chatterId: store.state.chatterId || "",
        name: newConversationName.value,
        participants: selectedChatters.value.map(chatter => chatter.id)
      })
    }
    const addChatter = (chatter: Chatter) => {
      selectedChatters.value.push(chatter)
    }

    watch([newChatterName, selectedChatters], ([chatterName]) => {
      const notCurrentChatter = (chatter: Chatter, currentChatterId: ChatterId) => chatter.id !== currentChatterId;
      const notAlreadyAdded = (chatter: Chatter, chatters: Chatter[]) => !chatters.find(selectedChatter => chatter.id === selectedChatter.id);

      const nextSelectableChatters = []
      if (chatterName.length === 0) {
        selectableChatters.value = []
      } else {
        for (let chatter of store.state.chatters.values()) {
          if (chatter.name.includes(chatterName)) {
            nextSelectableChatters.push(chatter)
          }
        }
        selectableChatters.value = nextSelectableChatters
            .filter(chatter => notCurrentChatter(chatter, store.state.chatterId || ""))
            .filter(chatter => notAlreadyAdded(chatter, selectedChatters.value))
      }
    }, {
      deep: true
    })
    return {
      newConversationName,
      newChatterName,
      addChatter,
      selectedChatters,
      selectableChatters,
      createConversation,
    }
  }

});
</script>

<style scoped lang="scss">
.conversation-creation {
  margin: auto;
  width: 100%;
  border: 1px solid black;
}
</style>
