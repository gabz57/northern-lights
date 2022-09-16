<template>
  <ChatApp />
</template>

<script lang="ts">
import ChatApp from "@/components/chat/Chat.vue";
import { defineComponent } from "vue";

export default defineComponent({
  name: "Home",
  components: { ChatApp },
});
</script>
