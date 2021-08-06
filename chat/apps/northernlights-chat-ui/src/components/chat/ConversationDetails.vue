<template>
  <div>
    Name: {{details.name}}<br/>
    Created:<br/>
    By: {{details.createdBy}}<br/>
    At: {{$filters.dateTime(details.createdAt * 1000)}}
  </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue";
import {ConversationDetails} from "@/composables/use-conversation";

export default defineComponent({
  name: "ConversationDetails",
  props: {
    details: {
      type: Object as PropType<ConversationDetails>,
      required: true
    },
  },
});

</script>

<style scoped lang="scss">
</style>
