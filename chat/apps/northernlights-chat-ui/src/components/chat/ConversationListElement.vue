<template>
  <div class="conversation-list-element"
       :class="{'conversation-list-element--with-new-message': details.nbUnreadMessages > 0}"
       @click="$emit('select-conversation', details.id)">
    <span>{{ details.name }}</span>
    <span>{{ details.nbUnreadMessages }}</span>
  </div>
</template>

<script lang="ts">
import {defineComponent, toRef} from "vue";
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
    return {
      ...useConversationDetails(toRef(props, 'conversationId'))
    }
  }
});

</script>

<style scoped lang="scss">
.conversation-list-element {
  display: flex;
  justify-content: space-between;
  padding: 0.4rem 1rem;
  align-items: center;

  &--with-new-message {
    font-weight: bold;
  }
  &:hover {
    cursor: pointer;
    border-radius: 2rem;
    background-color: lightblue;
  }
}
</style>
