import type { Ref } from "vue";
import { computed } from "vue";
import type { ChatterId } from "@/domain/model";
import { userApiClient } from "@/services/UserApiClient";
import { useUserStore } from "@/stores/user";

export default function useSseSubscriber(): {
  jwt: Ref<string>;
  subscribe: () => Promise<void>;
} {
  const userStore = useUserStore();
  const chatterIdRef = computed(() => userStore.chatterId);
  const setChatterId = (chatterId: ChatterId) =>
    userStore.setChatterId(chatterId);

  const subscribe = async () => {
    console.log("use-sse-subscriber> subscribe ?");
    if (!chatterIdRef.value || chatterIdRef.value === "0") {
      console.log("use-sse-subscriber> subscribe");
      const chatterId = await userApiClient
        .subscribe()
        .catch((e) => console.log(e));
      if (chatterId != null) {
        setChatterId(chatterId);
      }
    } else {
      console.log("use-sse-subscriber> skip", chatterIdRef.value);
    }
  };

  return {
    jwt: computed(() => userStore.jwt),
    subscribe,
  };
}
