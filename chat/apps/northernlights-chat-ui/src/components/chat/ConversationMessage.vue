<template>
  <div class="conversation-message">
    <div class="conversation-message__content">> {{ data.value.message }}</div>
  </div>
</template>

<script lang="ts">
import type { ComputedRef, PropType, Ref } from "vue";
import { computed, defineComponent, toRef } from "vue";
import type {
  ConversationDataWithMarkers,
  Markers,
} from "@/composables/use-conversation";
import type { ConversationMessageData } from "@/domain/model";

export default defineComponent({
  name: "ConversationMessage",
  props: {
    messageData: {
      type: Object as PropType<ConversationDataWithMarkers>,
      required: true,
    },
  },
  setup(props) {
    return {
      data: computed(() => toRef(props, "messageData")) as ComputedRef<
        Ref<ConversationMessageData & Markers>
      >,
    };
  },
});
</script>

<style scoped lang="scss">
.conversation-message {
  &__content {
    max-width: fit-content;
  }
}
</style>
