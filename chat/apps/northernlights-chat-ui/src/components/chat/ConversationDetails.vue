<template>
  <div class="conversation-details">
    <h2>Info</h2>
    <div style="text-align: left">
      <div>
        <strong>Name: </strong>{{ details.name }}<br/>
        <strong>Created: </strong><br/>
        <strong>By: </strong><Chatter v-if="details.createdBy" :chatter-id="details.createdBy" /><br/>
        <strong>At: </strong>{{ $filters.dateTime(details.createdAt * 1000) }}
      </div>
      <div>
        <strong>Members: </strong><br/>
        <span v-for="chatterId in details.participants" :key="chatterId">
          - <Chatter :chatter-id="chatterId" /><br/>
        </span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue";
import {ConversationDetails} from "@/composables/use-conversation-details";
import Chatter from "@/components/chat/Chatter.vue";

export default defineComponent({
  name: "ConversationDetails",
  components: {Chatter},
  props: {
    details: {
      type: Object as PropType<ConversationDetails>,
      required: true
    },
  },
});

</script>

<style scoped lang="scss">
.conversation-details {
  padding: 10px;
}
</style>
