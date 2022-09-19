<template>
  <div class="conversation-read-marker">
    {{ face }}
    <div class="conversation-read-marker__tooltip">{{ details.name }}</div>
  </div>
</template>

<script lang="ts">
import { defineComponent, toRefs } from "vue";
import useChatter from "@/composables/use-chatter";
import useChatterFace from "@/composables/use-chatter-face";

export default defineComponent({
  name: "ConversationReadMarker",
  props: {
    chatterId: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const { chatterId } = toRefs(props);
    return {
      ...useChatter(chatterId),
      ...useChatterFace(chatterId),
    };
  },
});
</script>

<style scoped lang="scss">
.conversation-read-marker {
  &__tooltip {
    visibility: hidden;
    width: max-content;
    background-color: black;
    color: #fff;
    text-align: center;
    padding: 5px;
    border-radius: 6px;
    position: absolute;
    bottom: 100%;
    right: 0;
    z-index: 1;
    opacity: 0;
  }

  &:hover & {
    &__tooltip {
      visibility: visible;
      opacity: 1;
      transition: opacity 1.3s;
    }
  }
}
</style>
