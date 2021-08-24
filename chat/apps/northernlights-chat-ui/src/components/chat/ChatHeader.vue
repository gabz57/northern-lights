<template>
  <div class="chat-header">
    <div class="chat-header__connect" style="margin: auto;">
      <input v-model="inputChatterId" @keydown.enter="setChatterId(inputChatterId)"/>
      <button @click="setChatterId(inputChatterId)">CONNECT</button>
      <div class="chat-header__connect-status">
        &nbsp;{{ sseStatus.sseOpen ? "\uD83D\uDFE2" : "\u26AA" }}
        <div class="chat-header__connect-status-tooltip">
          <div>Sse AutoConnect : {{ sseStatus.sseAutoConnect }}</div>
          <div>Sse Wanted : {{ sseStatus.sseWanted }}</div>
          <div>Sse Open : {{ sseStatus.sseOpen }}</div>
        </div>
      </div>
      <div class="chat-header__connect-body" v-if="sseStatus.sseOpen" @click="toggleEditingProfile">{{ "\uD83D\uDC64" }}
        <div class="chat-header__connect-body-tooltip">
          <ChatterLabel v-if="profile" :chatter-id="profile?.id"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">

import {defineComponent, ref} from "vue";
import ChatterLabel from "@/components/chat/ChatterLabel.vue";
import useProfile from "@/composables/use-profile";
import useSseStatus from "@/composables/use-sse-status";

export default defineComponent({
  name: "ChatHeader",
  components: {ChatterLabel},
  setup() {
    return {
      inputChatterId: ref<string>(""),
      ...useSseStatus(),
      ...useProfile(),
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
        cursor: pointer;

        &-tooltip {
          visibility: visible;
        }
      }
    }
  }
}
</style>