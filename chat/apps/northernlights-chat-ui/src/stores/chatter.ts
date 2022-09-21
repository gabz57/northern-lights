import { defineStore } from "pinia";
import { computed } from "vue";
import type { Chatter, ChatterId, Chatters } from "@/domain/model";
import { useLocalStorage } from "@vueuse/core";

export type ChattersStore = ReturnType<typeof useChattersStore>;

export const useChattersStore = defineStore("chatters", () => {
  // state
  const chatters = useLocalStorage<Chatters>(
    "chatters",
    new Map<ChatterId, Chatter>(),
    {
      serializer: {
        read: (v: string): Chatters => {
          return v
            ? new Map<ChatterId, Chatter>(JSON.parse(v))
            : new Map<ChatterId, Chatter>();
        },
        write: (v: Chatters): string => {
          return JSON.stringify(Array.from(v.entries()));
        },
      },
    }
  );

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
