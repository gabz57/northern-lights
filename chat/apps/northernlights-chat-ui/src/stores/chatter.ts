import { defineStore } from "pinia";
import { computed, ref } from "vue";
import type { Chatter, ChatterId, Chatters } from "@/domain/model";

export type ChattersStore = ReturnType<typeof useChattersStore>;

export const useChattersStore = defineStore("chatters", () => {
  // state
  const chatters = ref<Chatters>(new Map<ChatterId, Chatter>());

  // getters
  const getChatterById = computed(() => (chatterId: ChatterId) => {
    const chatter: Chatter | undefined = chatters.value.get(chatterId);
    if (chatter == undefined) {
      throw new Error("Using unknown chatter id : " + chatterId);
    }
    return chatter;
  });

  // actions
  function installChatter(chatter: Chatter) {
    chatters.value.set(chatter.id, chatter);
  }

  return {
    chatters,
    getChatterById,
    installChatter,
  };
});
