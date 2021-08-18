<template>
  <div>
    <span style="color: #8599ad">{{ data.value.chatterId }} joined the conversation</span>
    <span v-if="data.value.readBy.length > 0"><br/>Read by [{{ data.value.readBy.join(", ") }}]</span>
  </div>
</template>

<script lang="ts">
import {computed, ComputedRef, defineComponent, PropType, Ref, toRefs} from "vue";
import {ConversationDataWithMarkers, Markers} from "@/composables/use-conversation";
import {ConversationChatterData, ConversationMessageData} from "@/store/state";

export default defineComponent({
  name: "ConversationChatter",
  props: {
    chatterData: {
      type: Object as PropType<ConversationDataWithMarkers>,
      required: true
    }
  },
  setup(props) {
    const {chatterData} = toRefs(props)

    return {
      data: computed(() => chatterData) as ComputedRef<Ref<ConversationChatterData & Markers>>
    }
  }
});

</script>

<style scoped>

</style>