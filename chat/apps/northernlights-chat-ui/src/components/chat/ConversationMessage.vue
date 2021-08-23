<template>
  <div class="conversation-message">
    <div class="conversation-message__content">> {{ data.value.message }}</div>
<!--    <span v-if="data.value.readBy.length > 0"><br/>Read by [{{ data.value.readBy.join(", ") }}]</span>-->
  </div>
</template>

<script lang="ts">
import {computed, ComputedRef, defineComponent, PropType, Ref, toRefs} from "vue";
import {ConversationDataWithMarkers, Markers} from "@/composables/use-conversation";
import {ConversationMessageData} from "@/store/state";

export default defineComponent({
  name: "ConversationMessage",
  props: {
    messageData: {
      type: Object as PropType<ConversationDataWithMarkers>,
      required: true
    }
  },
  setup(props) {
    const {messageData} = toRefs(props)

    return {
      data: computed(() => messageData) as ComputedRef<Ref<ConversationMessageData & Markers>>
    }
  }
});

</script>

<style scoped lang="scss">
.conversation-message {
  &__content {
    max-width: fit-content;
  }
}
</style>