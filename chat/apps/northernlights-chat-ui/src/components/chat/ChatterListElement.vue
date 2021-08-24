<template>
  <div v-if="details" class="chatter-list-element"
       :class="{'chatter-list-element--with-new-message': details.nbUnreadMessages > 0}"
       @click="openConversationWith">
    <ChatterLabel :chatter-id="chatterId" show-face/>
    <span>
      {{ details.nbUnreadMessages }}
    </span>
  </div>
</template>

<script lang="ts">
/* eslint-disable no-debugger */

import {defineComponent, toRef} from "vue";
import ChatterLabel from "@/components/chat/ChatterLabel.vue";
import useChatterConversationDetails from "@/composables/use-chatter-conversation-details";
import useChatterConversationOpener from "@/composables/use-chatter-conversation-opener";

export default defineComponent({
  name: "ChatterListElement",
  components: {ChatterLabel},
  props: {
    chatterId: {
      type: String,
      required: true
    }
  },
  setup: function (props) {
    const chatterId = toRef(props, 'chatterId')
    return {
      ...useChatterConversationOpener(chatterId),
      ...useChatterConversationDetails(chatterId)
    }
  }
});

</script>

<style scoped lang="scss">
.chatter-list-element {
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
