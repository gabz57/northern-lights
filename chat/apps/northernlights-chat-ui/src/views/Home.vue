<template>
  <ChatApp/>
</template>

<script lang="ts">
import { Options, Vue } from 'vue-class-component';
import ChatApp from "@/components/chat/Chat.vue"; // @ is an alias to /src

@Options({
  components: {
    ChatApp,
  },
})
export default class Home extends Vue {}
</script>
