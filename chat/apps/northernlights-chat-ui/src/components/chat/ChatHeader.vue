<template>
  <div class="chat-header">
    <input v-model="userId" @keydown.enter="setChatterId()"/>
    <button @click="setChatterId()">CONNECT</button>
    <div class="chat-header__status">
      &nbsp;{{ sseState.sseOpen ? "\uD83D\uDFE2" : "\u26AA" }}
      <div class="chat-header__status-tooltip">
        <div>Sse AutoConnect : {{ sseState.sseAutoConnect }}</div>
        <div>Sse Wanted : {{ sseState.sseWanted }}</div>
        <div>Sse Open : {{ sseState.sseOpen }}</div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">

import {defineComponent, ref} from "vue";
import {useStore} from "@/store";
import {ActionTypes} from "@/store/actions";
import useSseState from "@/composables/use-sse-state";

export default defineComponent({
  name: "ChatHeader",
  setup() {
    const store = useStore()
    const {state: sseState} = useSseState()
    const userId = ref<string>("")

    const setChatterId = () => {
      store.dispatch(ActionTypes.SetChatterId, {chatterId: userId.value});
    }

    return {
      sseState,
      userId,
      setChatterId,
    }
  }
})
</script>

<style scoped lang="scss">
.chat-header {
  &__status {
    position: relative;
    display: inline-block;

    &-tooltip {
      visibility: hidden;
      width: max-content;
      background-color: black;
      color: #fff;
      text-align: center;
      padding: 5px;
      border-radius: 6px;
      position: absolute;
      z-index: 1;
    }
    &:hover & {
      &-tooltip {
        visibility: visible;
      }
    }
  }
}
</style>