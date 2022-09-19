import { defineStore } from "pinia";
import type { ChatterId, Profile } from "@/domain/model";
import { ref } from "vue";
import { userApiClient } from "@/services/UserApiClient";
import { chatApiClient } from "@/services/ChatApiClient";
import { useSessionStorage } from "@vueuse/core";

export type UserStore = ReturnType<typeof useUserStore>;

export const useUserStore = defineStore("user", () => {
  // state
  // const jwt = ref<string>("");
  // const chatterId = ref<ChatterId>("0");
  const jwt = useSessionStorage("user.jwt", "");
  const chatterId = ref<ChatterId>("0");
  if (jwt.value !== "") {
    // TODO: use injection
    userApiClient.setJwt(jwt.value);
    chatApiClient.setJwt(jwt.value);
  }
  const profile = ref<Profile>();

  function setChatterId(id: ChatterId) {
    chatterId.value = id;
  }

  function setJwt(token: string) {
    jwt.value = token;
    // TODO: use injection
    userApiClient.setJwt(token);
    chatApiClient.setJwt(token);
  }

  function installProfile(p: Profile) {
    profile.value = p;
  }

  return {
    jwt,
    chatterId,
    profile,
    setChatterId,
    setJwt,
    installProfile,
  };
});
