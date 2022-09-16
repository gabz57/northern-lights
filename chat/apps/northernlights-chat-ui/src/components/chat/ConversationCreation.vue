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
import {defineComponent, ref, watch} from "vue";
import {Chatter, ChatterId} from "@/domain/model";
import {useUserStore} from "@/stores/user";
import {useChattersStore} from "@/stores/chatter";
import {useConversationsStore} from "@/stores/conversations";

export default defineComponent({
  name: "ConversationCreation",
  setup() {
    const userStore = useUserStore();
    const chattersStore = useChattersStore();
    const conversationsStore = useConversationsStore();
    const selectedChatters = ref<Chatter[]>([])
    const selectableChatters = ref<Chatter[]>([])
    const newConversationName = ref<string>("")
    const newChatterName = ref<string>("")
    const createConversation = () => {
      conversationsStore.createConversation(
         userStore.chatterId || "",
         newConversationName.value,
         selectedChatters.value.map(chatter => chatter.id),
         false
      )
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
        // TODO: use chatter search
        for (const chatter of chattersStore.chatters.values()) {
          if (chatter.name.includes(chatterName)) {
            nextSelectableChatters.push(chatter)
          }
        }
        selectableChatters.value = nextSelectableChatters
            .filter(chatter => notCurrentChatter(chatter, userStore.chatterId || ""))
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
