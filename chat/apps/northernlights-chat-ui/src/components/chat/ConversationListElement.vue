<template>
  <div class="conversation-list-element"
       :class="{'conversation-list-element--with-new-message': details.nbUnreadMessages > 0}"
       @click="$emit('select-conversation', details.id)">
    {{ details.name + " : " + details.nbUnreadMessages }}
  </div>
</template>

<script lang="ts">
import {defineComponent, toRefs} from "vue";
import useConversationDetails from "@/composables/use-conversation-details";

export default defineComponent({
  name: "ConversationListElement",
  props: {
    conversationId: {
      type: String,
      required: true
    },
  },
  setup(props) {
    const {conversationId} = toRefs(props)

    return {
      details: useConversationDetails(conversationId).details
    }
  }
});

</script>

<style scoped lang="scss">
.conversation-list-element {
  &--with-new-message {
    font-weight: bold;
  }
}
</style>
