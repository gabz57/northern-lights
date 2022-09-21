import { computed, nextTick, watch } from "vue";
import type {
  ChatterId,
  Conversation,
  ConversationDataId,
  ConversationId,
} from "@/domain/model";
import { chatApiClient } from "@/services/ChatApiClient";
import SseChatService from "@/services/SseChatService";
import type { SseStatus } from "@/composables/use-sse-status";
import useSseStatus from "@/composables/use-sse-status";
import { userApiClient } from "@/services/UserApiClient";
import { useSseStore } from "@/stores/sse";
import { useUserStore } from "@/stores/user";
import { useUiStore } from "@/stores/ui";
import { useConversationsStore } from "@/stores/conversations";
import { useChattersStore } from "@/stores/chatter";
import { useJwt } from "@vueuse/integrations/useJwt";
import { DateTime } from "luxon";

export default function useSse(): {
  disconnect: () => Promise<void>;
  enableAutoConnect: () => void;
  disableAutoConnect: () => void;
  state: SseStatus;
} {
  console.log("useSse()");
  const sseStore = useSseStore();
  const userStore = useUserStore();
  const uiStore = useUiStore();
  const conversationsStore = useConversationsStore();

  const { sseStatus } = useSseStatus();
  const jwtRef = computed(() => userStore.jwt);
  const chatterIdRef = computed(() => userStore.chatterId);
  const enableSseWanted = () => sseStore.enableSseWanted();
  const disableSseWanted = () => sseStore.disableSseWanted();
  const enableAutoConnect = () => sseStore.enableSseAutoConnect();
  const disableAutoConnect = () => sseStore.disableSseAutoConnect();
  const storeSseOpenStatus = (isOpen: boolean) =>
    sseStore.setSseOpenStatus(isOpen);
  const storeEventSource = (eventSource: EventSource | undefined) =>
    sseStore.setEventSource(eventSource);
  const setChatterId = (chatterId: ChatterId) =>
    userStore.setChatterId(chatterId);
  const switchSseChatter = async (
    chatterId: ChatterId | undefined,
    prevChatterId: ChatterId | undefined
  ) => {
    console.log("use-sse> switchSseChatter ", prevChatterId, " -> ", chatterId);
    if (prevChatterId !== undefined && prevChatterId !== "0") {
      uiStore.deselectConversationId();
      await nextTick(() => {
        sseStore.clearState();
      });
      await nextTick(() => {
        disconnect();
      });
    }
    if (chatterId !== undefined && chatterId !== "" && chatterId !== "0") {
      await connect();
    }
  };
  watch(() => chatterIdRef.value, switchSseChatter);

  const onJwtChange = async (
    jwt: string | undefined,
    previousJwt: string | undefined
  ) => {
    console.log("use-sse> onJwtChange ", previousJwt, " -> ", jwt);
    // clear all when token expire
    if (!!previousJwt && !jwt) {
      uiStore.deselectConversationId();
      await nextTick(() => {
        sseStore.clearState();
      });
      await nextTick(() => {
        disconnect();
      });
    }

    if (jwt) {
      const decodedJwt = useJwt(jwt);
      if (decodedJwt.payload.value?.exp !== undefined) {
        const exp = DateTime.fromSeconds(decodedJwt.payload.value?.exp);
        if (exp < DateTime.now()) {
          userStore.setJwt("");
          return;
        }
      }
      try {
        const chatterId = await userApiClient.userInfo();
        if (chatterId && chatterId !== "") {
          await nextTick(() => {
            setChatterId(chatterId);
          });
          // .then(connect)
        } else {
          // TODO: no chatterId => ask to register
        }
      } catch (e: any) {
        console.log(e);
      }
    }
  };
  watch(() => jwtRef.value, onJwtChange, {
    immediate: true,
  });

  const eventSource = computed(() => sseStore.eventSource);

  const disconnect = async () => {
    console.log("use-sse> disconnect");
    if (eventSource.value) {
      disableAutoConnect();
      disableSseWanted();
    }
  };

  const connect = async () => {
    console.log("use-sse> connect");
    if (chatterIdRef.value) {
      enableAutoConnect();
      enableSseWanted();
    }
  };

  // watched :
  // - sseStatus.sseWanted
  // - sseStatus.sseOpen (when sseStatus.sseWanted = true)
  const doConnect = async () => {
    const onOpen = async () => {
      console.log("use-sse> SSE opened");
      storeSseOpenStatus(true);
    };
    const onReconnection = async () => {
      console.log("use-sse> Reconnecting SSE...");
      await doDisconnect();
      storeSseOpenStatus(false);
    };
    const onConnectionClosed = async () => {
      console.log("use-sse> SSE connection CLOSED");
      disableSseWanted();
      storeSseOpenStatus(false);
    };
    const conversationStatuses: () => Map<
      ConversationId,
      ConversationDataId
    > = () => {
      const statuses = new Map<ConversationId, ConversationDataId>();
      conversationsStore.conversations.forEach(
        (conversation: Conversation, conversationId: ConversationId) => {
          if (conversation.data.length > 0) {
            statuses.set(
              conversationId,
              conversation.data[conversation.data.length - 1].id
            );
          }
        }
      );
      return statuses;
    };
    const sseChatKey = await chatApiClient.initSse(conversationStatuses());

    try {
      const sseEventSource = SseChatService.openSse(
        sseChatKey,
        jwtRef.value,
        onOpen,
        onReconnection,
        onConnectionClosed
      );
      storeEventSource(sseEventSource);
      SseChatService.bind(
        sseEventSource,
        userStore,
        useConversationsStore(),
        useChattersStore()
      );
    } catch (e: any) {
      //
      console.log("⚠️ SseChatService.openSse failed", e);
    }
  };
  const doDisconnect = async () => {
    if (eventSource.value !== undefined) {
      eventSource.value.close();
      storeEventSource(undefined);
    }
  };

  watch(
    () => sseStatus.sseWanted,
    async (sseWanted: boolean, prevSseWanted: boolean) => {
      console.log("state.sseWanted", prevSseWanted, " -> ", sseWanted);
      if (sseWanted !== prevSseWanted) {
        if (sseWanted) {
          if (jwtRef.value) {
            await doConnect();
          }
        } else {
          if (eventSource.value) {
            await doDisconnect();
          }
        }
      }
    }
  );
  watch(
    () => uiStore.online,
    (isOnline: boolean, wasOnline: boolean) => {
      console.log("domain.state.ui.online", wasOnline, " -> ", isOnline);

      if (isOnline) {
        if (sseStore.sseAutoConnect) {
          console.log("Device went ONLINE > re-enable SSE");
          enableSseWanted();
        } else {
          console.log("Device went ONLINE > do nothing");
        }
      } else {
        // device went offline
        console.log("Device went OFFLINE > sseWanted = false");
        disableSseWanted();
      }
    }
  );

  const sseWantedRef = computed(() => sseStatus.sseWanted);
  watch(
    () => sseStatus.sseOpen,
    async (sseOpen: boolean, prevSseOpen: boolean) => {
      console.log("state.sseOpen", prevSseOpen, " -> ", sseOpen);
      if (prevSseOpen && !sseOpen && sseWantedRef.value && jwtRef.value) {
        await doConnect();
      }
    }
  );

  return {
    state: sseStatus,
    disconnect,
    enableAutoConnect,
    disableAutoConnect,
  };
}
