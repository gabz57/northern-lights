<template>
  <ul>
    <li v-for="(chatter) in chatters" :key="chatter.id">
      {{ "Chatter: " + chatter.id + " : " + chatter.name }}
    </li>
  </ul>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "ChatterList",
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
