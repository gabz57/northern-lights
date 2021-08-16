<template>
  <div class="chatter-list">
    <ChatterListElement v-for="(chatter) in chatters" :key="chatter.id"
                        :chatter-id="chatter.id" />
  </div>
</template>

<script lang="ts">
/* eslint-disable no-debugger */

import {defineComponent} from "vue";
import ChatterListElement from "@/components/chat/ChatterListElement.vue";

export default defineComponent({
  name: "ChatterList",
  components: {ChatterListElement},
  props: {
    chatters: {
      type: Array,
      required: true
    },
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
