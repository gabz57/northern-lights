<template>
  <div class="chat-header">
    <div class="chat-header__connect" style="margin: auto;">
      <input v-model="userId" @keydown.enter="setChatterId()"/>
      <button @click="setChatterId()">CONNECT</button>
      <div class="chat-header__connect-status">
        &nbsp;{{ sseState.sseOpen ? "\uD83D\uDFE2" : "\u26AA" }}
        <div class="chat-header__connect-status-tooltip">
          <div>Sse AutoConnect : {{ sseState.sseAutoConnect }}</div>
          <div>Sse Wanted : {{ sseState.sseWanted }}</div>
          <div>Sse Open : {{ sseState.sseOpen }}</div>
        </div>
      </div>
      <span class="chat-header__connect-body" v-if="sseState.sseOpen" @click="toggleEditingProfile">{{"\uD83D\uDC64"}}</span>
    </div>
  </div>
</template>

<script lang="ts">

import {computed, defineComponent, ref} from "vue";
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
    const editingProfile = computed(() => store.state.ui.editingProfile)

    const toggleEditingProfile = () => {
      store.dispatch(ActionTypes.SetEditingProfile, !editingProfile.value);
    }

    return {
      sseState,
      userId,
      setChatterId,
      toggleEditingProfile,
    }
  }
})
</script>

<style scoped lang="scss">
.chat-header {
  display: flex;

  &__connect {
    &-status {
      margin: auto;
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
    &-body {
      &:hover {
        cursor: pointer;
      }
    }
  }
}
</style>