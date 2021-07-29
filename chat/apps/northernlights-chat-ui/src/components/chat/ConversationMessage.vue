<template>
  <div>From '{{ message.author }}' : {{ message.message }}</div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue";
import {ConversationData} from "@/store/state";

export default defineComponent({
  name: "ConversationMessage",
  props: {
    message: {
      type: Object as PropType<ConversationData>,
      required: true
    }
  },
  // setup(props) {
  //
  //   return {
  //
  //   }
  // }
});

</script>

<style scoped>

</style>