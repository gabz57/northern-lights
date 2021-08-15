<template>
  {{ details.name }}
</template>

<script lang="ts">
import {defineComponent, toRefs} from "vue";
import useChatter from "@/composables/use-chatter";

export default defineComponent({
  name: "Chatter",
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

<style scoped>

</style>