<template>
  <div class="chatter-list">
    <h3>- Connus -</h3>
    <ChatterListElement
      v-for="chatterId in chatterIds"
      :key="chatterId"
      :chatter-id="chatterId"
    />
    <h3>- Connectés -</h3>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import ChatterListElement from "@/components/chat/leftmenu/ChatterListElement.vue";
import useChatters from "@/composables/use-chatters";

export default defineComponent({
  name: "ChatterList",
  components: { ChatterListElement },
  setup() {
    return {
      ...useChatters(),
    };
  },
});
</script>

<style scoped lang="scss">
.chatter-list {
  &:hover {
    cursor: pointer;
  }
}
</style>
