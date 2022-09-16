import { defineStore } from "pinia";
import { ref } from "vue";
import { useChattersStore } from "@/stores/chatter";
import { useConversationsStore } from "@/stores/conversations";

export const useSseStore = defineStore("sse", () => {
  // state
  const eventSource = ref<EventSource>();
  const sseAutoConnect = ref<boolean>(false);
  const sseWanted = ref<boolean>(false);
  const sseOpen = ref<boolean>(false);

  // actions
  function setSseOpenStatus(isOpen: boolean) {
    console.log("sseStore.sseOpen <-", isOpen);
    sseOpen.value = isOpen;
  }

  function setEventSource(es: EventSource | undefined) {
    eventSource.value = es;
  }

  function enableSseWanted() {
    console.log("sseStore.sseWanted <-", true);
    sseWanted.value = true;
  }

  function disableSseWanted() {
    console.log("sseStore.sseWanted <-", false);
    sseWanted.value = false;
  }

  function enableSseAutoConnect() {
    console.log("sseStore.sseAutoConnect <-", true);
    sseAutoConnect.value = true;
  }

  function disableSseAutoConnect() {
    console.log("sseStore.sseAutoConnect <-", false);
    sseAutoConnect.value = false;
  }

  function clearState() {
    // useUserStore().$reset()
    useChattersStore().chatters.clear();
    useConversationsStore().conversations.clear();
  }

  return {
    eventSource,
    sseAutoConnect,
    sseWanted,
    sseOpen,
    setSseOpenStatus,
    setEventSource,
    enableSseWanted,
    disableSseWanted,
    enableSseAutoConnect,
    disableSseAutoConnect,
    clearState,
  };
});
