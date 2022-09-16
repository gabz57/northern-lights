<template>
  <div class="chat-header">
    <div class="chat-header__connect" style="margin: auto">
      <button v-if="chatterId === '0'" @click="subscribe">SUBSCRIBE</button>
      <div class="chat-header__connect-status">
        &nbsp;{{ sseStatus.sseOpen ? "\uD83D\uDFE2" : "\u26AA" }}
        <div
          v-if="chatterId !== '0'"
          class="chat-header__connect-status-tooltip"
        >
          <div>Sse AutoConnect : {{ sseStatus.sseAutoConnect }}</div>
          <div>Sse Wanted : {{ sseStatus.sseWanted }}</div>
          <div>Sse Open : {{ sseStatus.sseOpen }}</div>
        </div>
      </div>
      <div
        class="chat-header__connect-body"
        v-if="sseStatus.sseOpen"
        @click="toggleEditingProfile"
      >
        {{ "\uD83D\uDC64" }}
        <div v-if="profile?.id" class="chat-header__connect-body-tooltip">
          <ChatterLabel :chatter-id="profile?.id" />
        </div>
      </div>
      <GoogleAuthSignIn />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import ChatterLabel from "@/components/chat/ChatterLabel.vue";
import useProfile from "@/composables/use-profile";
import useSseStatus from "@/composables/use-sse-status";
import GoogleAuthSignIn from "@/components/GoogleAuthSignIn.vue";
import useSseSubscriber from "@/composables/use-sse-subscriber";

export default defineComponent({
  name: "ChatHeader",
  components: { ChatterLabel, GoogleAuthSignIn },
  setup() {
    return {
      inputChatterId: ref<string>(""),
      ...useSseStatus(),
      ...useProfile(),
      ...useSseSubscriber(),
    };
  },
});
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
