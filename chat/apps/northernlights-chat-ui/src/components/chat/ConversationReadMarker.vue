<template>
  <div class="conversation-read-marker">
    {{
      chatterId === "1" ? "❤️"
          : chatterId === "2" ? "😎"
              : chatterId === "3" ? "🐈"
                  : chatterId === "4" ? "🍃"
                      : chatterId === "5" ? "🥶"  : "😈"
    }}
    <div class="conversation-read-marker__tooltip">{{ details.name }}</div>
  </div>
</template>

<script lang="ts">
import {defineComponent, toRefs} from "vue";
import useChatter from "@/composables/use-chatter";

export default defineComponent({
  name: "ConversationReadMarker",
  props: {
    chatterId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const {chatterId} = toRefs(props)
    const {details} = useChatter(chatterId)
    return {
      details
    }
  }
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