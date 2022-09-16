<template>
  <div>
    <span style="color: #8599ad">
      <ChatterLabel :chatter-id="chatterData.chatterId" /> joined the conversation
    </span>
  </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue";
import {ConversationChatterData} from "@/domain/model";
import ChatterLabel from "@/components/chat/ChatterLabel.vue";

export default defineComponent({
  name: "ConversationChatter",
  components: {ChatterLabel},
  props: {
    chatterData: {
      type: Object as PropType<ConversationChatterData>,
      required: true
    }
  },
});

</script>

<style scoped lang="scss">

</style>