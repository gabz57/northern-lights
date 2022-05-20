<template>
  <div class="chatter-list">
    <h3>- Connus -</h3>
    <ChatterListElement v-for="(chatterId) in chatterIds" :key="chatterId" :chatter-id="chatterId"/>
    <h3>- Connectés -</h3>


  </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue";
import ChatterListElement from "@/components/chat/ChatterListElement.vue";
import {ChatterId} from "@/store/state";

export default defineComponent({
  name: "ChatterList",
  components: {ChatterListElement},
  props: {
    chatterIds: {
      type: Object as PropType<ChatterId[]>,
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
