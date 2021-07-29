import {computed, reactive, watch} from "vue";
import {ChatterId} from "@/store/state";
import {useStore} from "@/store";
import {chatApiClient} from "@/ChatApiClient";
import SseChatService from "@/SseChatService";
import {ActionTypes} from "@/store/actions";

type SseStatus = {
    sseAutoConnect: boolean,
    sseWanted: boolean,
    sseOpen: boolean,
}

export default function useSse() {

    const store = useStore()
    const chatterIdRef = computed(() => store.state.chatterId)
    const enableSseWanted = () => store.dispatch(ActionTypes.EnableSseWanted)
    const disableSseWanted = () => store.dispatch(ActionTypes.DisableSseWanted)
    const enableAutoConnect = () => store.dispatch(ActionTypes.EnableSseAutoConnect)
    const disableAutoConnect = () => store.dispatch(ActionTypes.DisableSseAutoConnect)

    const switchSseChatter = async (chatterId: ChatterId | undefined, prevChatterId: ChatterId | undefined) => {
        console.log("switchSseChatter ", prevChatterId, " -> ", chatterId)
        if (prevChatterId !== undefined && prevChatterId !== "0") {
            await disconnect();
            store.dispatch(ActionTypes.ClearChatterState)
        }
        if (chatterId !== undefined && chatterId !== "" && chatterId !== "0") {
            await connect();
        }
    }
    watch(chatterIdRef, switchSseChatter)

    const eventSource = computed(() => store.state.sse.eventSource)

    const disconnect = async () => {
        console.log('disconnect');
        if (eventSource.value) {
            disableAutoConnect();
            disableSseWanted();
        }
    }

    const connect = async () => {
        console.log('connect');
        if (chatterIdRef.value) {
            enableAutoConnect()
            enableSseWanted()
        }
    }
    const sseAutoConnectRef = computed(() => store.state.sse.sseAutoConnect)

    const doConnect = async (chatterId: ChatterId) => {
        const onOpen = async () => store.dispatch(ActionTypes.StoreSseOpenStatus, true)
        const onReconnection = async () => {
            console.log('use-sse> Reconnecting...');
            await doDisconnect()
            store.dispatch(ActionTypes.StoreSseOpenStatus, false);
        }
        const onConnectionClosed = async () => {
            console.log('use-sse> Connection failed, will not reconnect');
            store.dispatch(ActionTypes.DisableSseWanted);
            store.dispatch(ActionTypes.StoreSseOpenStatus, false);
        }
        const sseChatKey = await chatApiClient.authent(chatterId);
        const sseEventSource = SseChatService.openSse(sseChatKey, onOpen, onReconnection, onConnectionClosed);
        store.dispatch(ActionTypes.StoreEventSource, sseEventSource);
        SseChatService.bind(sseEventSource, store);
    }
    const doDisconnect = async () => {
        eventSource.value?.close();
        store.dispatch(ActionTypes.StoreEventSource, undefined);
    }

    const sseWantedRef = computed(() => store.state.sse.sseWanted)
    const sseWantedWatcher = async (sseWanted: boolean, prevSseWanted: boolean) => {
        console.log("sseWantedWatcher", prevSseWanted, " -> " , sseWanted)
        if (sseWanted !== prevSseWanted) {
            if (sseWanted) {
                if (chatterIdRef.value) {
                    await doConnect(chatterIdRef.value)
                }
            } else {
                if (eventSource.value) {
                    await doDisconnect();
                }
            }
        }
    }
    watch(sseWantedRef, sseWantedWatcher)

    const sseOpenRef = computed(() => store.state.sse.sseOpen)
    const sseOpenWatcher = async (sseOpen: boolean, prevSseOpen: boolean) => {
        console.log("sseOpenWatcher", prevSseOpen, " -> " , sseOpen)
        if (prevSseOpen && !sseOpen && sseWantedRef.value && chatterIdRef.value) {
            await doConnect(chatterIdRef.value)
        }
    }
    watch(sseOpenRef, sseOpenWatcher)

    const state: SseStatus = reactive({
        sseAutoConnect: sseAutoConnectRef,
        sseWanted: sseWantedRef,
        sseOpen: sseOpenRef,
    })

    return {
        state,
        disconnect,
        enableAutoConnect,
        disableAutoConnect,
    };
}
