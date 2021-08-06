<template>
  <div>
    <span>At {{ $filters.time(message.dateTime * 1000) }} :</span>
    <span>> {{ message.message }}</span>
    <span v-if="message.readBy.length > 0"><br />Read by [{{ message.readBy.join(", ") }}]</span>
  </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue";
import {ConversationDataWithMarkers} from "@/composables/use-conversation";

export default defineComponent({
  name: "ConversationMessage",
  props: {
    message: {
      type: Object as PropType<ConversationDataWithMarkers>,
      required: true
    }
  },
});

</script>

<style scoped>

</style>