<template>
  <ul>
    <li v-for="(chatter) in chatters" :key="chatter.id" style="text-align: left;">
      <Chatter :chatter-id="chatter.id" />
    </li>
  </ul>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import Chatter from "@/components/chat/Chatter.vue";

export default defineComponent({
  name: "ChatterList",
  components: {Chatter},
  props: {
    chatters: {
      type: Array,
      required: true
    },
  },
});

</script>

<style scoped lang="scss">
</style>
