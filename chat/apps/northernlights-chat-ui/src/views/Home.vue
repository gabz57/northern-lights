<template>
  <Chat/>
</template>

<script lang="ts">
import { Options, Vue } from 'vue-class-component';
import Chat from "@/components/chat/Chat.vue"; // @ is an alias to /src

@Options({
  components: {
    Chat,
  },
})
export default class Home extends Vue {}
</script>
