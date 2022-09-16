<template>
  <span class="chatter"
        :class="{'chatter--clickable': !self}"
        @click="openConversationWith">{{ showFace ? face + " " : "" }}{{ details.name }}</span>
</template>

<script lang="ts">
import {computed, defineComponent, toRef} from "vue";
import useChatter from "@/composables/use-chatter";
import useChatterConversationOpener from "@/composables/use-chatter-conversation-opener";
import useChatterFace from "@/composables/use-chatter-face";
import {useUserStore} from "@/stores/user";

export default defineComponent({
  name: "ChatterLabel",
  props: {
    chatterId: {
      type: String,
      required: true
    },
    showFace: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  setup(props) {
    const userStore = useUserStore();
    const chatterId = toRef(props, 'chatterId')
    return {
      self: computed(() => chatterId.value === userStore.chatterId),
      ...useChatter(chatterId),
      ...useChatterConversationOpener(chatterId),
      ...useChatterFace(chatterId)
    }
  }
});

</script>

<style scoped lang="scss">
.chatter {
  &--clickable {
    &:hover {
      cursor: pointer;
    }
  }
}
</style>