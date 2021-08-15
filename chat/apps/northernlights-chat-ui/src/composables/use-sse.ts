import {computed, watch} from "vue";
import {ChatterId, Conversation, ConversationDataId, ConversationId} from "@/store/state";
import {useStore} from "@/store";
import {chatApiClient} from "@/services/ChatApiClient";
import SseChatService from "@/services/SseChatService";
import {ActionTypes} from "@/store/actions";
import useSseState from "@/composables/use-sse-state";

export default function useSse() {

    const store = useStore()
    const { state } = useSseState()
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
        const conversationStatuses: () => Map<ConversationId, ConversationDataId> = () => {
            const statuses = new Map<ConversationId, ConversationDataId>()
            store.state.conversations.forEach((conversation: Conversation, conversationId: ConversationId) => {
                if (conversation.data.length > 0) {
                    statuses.set(conversationId, conversation.data[conversation.data.length - 1].id)
                }
            })
            return statuses
        }
        const sseChatKey = await chatApiClient.authent(chatterId, conversationStatuses);
        const sseEventSource = SseChatService.openSse(sseChatKey, onOpen, onReconnection, onConnectionClosed);
        store.dispatch(ActionTypes.StoreEventSource, sseEventSource);
        SseChatService.bind(sseEventSource, store);
    }
    const doDisconnect = async () => {
        eventSource.value?.close();
        store.dispatch(ActionTypes.StoreEventSource, undefined);
    }

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
    watch(() => state.sseWanted, sseWantedWatcher)

    const sseWantedRef = computed(() => state.sseWanted)
    const sseOpenWatcher = async (sseOpen: boolean, prevSseOpen: boolean) => {
        console.log("sseOpenWatcher", prevSseOpen, " -> " , sseOpen)
        if (prevSseOpen && !sseOpen && sseWantedRef.value && chatterIdRef.value) {
            await doConnect(chatterIdRef.value)
        }
    }
    watch(() => state.sseOpen, sseOpenWatcher)

    return {
        state,
        disconnect,
        enableAutoConnect,
        disableAutoConnect,
    };
}
