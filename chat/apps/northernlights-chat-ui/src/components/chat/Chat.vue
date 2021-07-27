<template>
  <div>
    <div>
      <h1>Connexion</h1>
      <input v-model="userId"/>
      <button @click="connectUser()">CONNECT</button>
    </div>
    <div>
      <h1>Chat</h1>
      <h2>Chatters</h2>
      <ul>
        <li v-for="(chatter, chatterIndex) in chatters" :key="chatterIndex">
          {{ "Chatter: " + chatter.id + " : " + chatter.name }}
        </li>
      </ul>
      <h2>Conversations</h2>
      <ul>
        <li v-for="(conversation, conversationIndex) in conversations" :key="conversationIndex">
          {{ "Conversation: " + conversation.id + " : " + conversation.name }}
          <ul>
            <li v-for="(event, eventIndex) in conversation.events" :key="eventIndex">
              {{ "Event: " + event.id + " - ..." }}
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import {Options, setup, Vue} from "vue-class-component";
import {useStore} from "@/store";
import {Chatter, Conversation} from "@/store/state";
import {sseChatService} from "@/SseChatService";

@Options({
  // Toutes les options de composant sont autorisées ici.
})
export default class Chat extends Vue {
  userId!: string
  chatters!: [Chatter]
  conversations!: [Conversation]

  store = setup(() => useStore())

  async connectUser() {
    const sseChatKey = await sseChatService.authent(this.userId);
    const eventSource = sseChatService.openSse(sseChatKey);
    sseChatService.listen(eventSource, this.store);
  }

  //
  // get fullName(): string {
  //   return `${this.firstName} ${this.lastName}`;
  // }
}
</script>

<style scoped lang="scss">
</style>
