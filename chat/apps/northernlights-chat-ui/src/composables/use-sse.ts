import {computed, nextTick, watch} from "vue";
import {ChatterId, Conversation, ConversationDataId, ConversationId} from "@/store/state";
import {useStore} from "@/store";
import {chatApiClient} from "@/services/ChatApiClient";
import SseChatService from "@/services/SseChatService";
import {ActionTypes} from "@/store/actions";
import useSseStatus, {SseStatus} from "@/composables/use-sse-status";

export default function useSse(): {
    disconnect: () => Promise<void>;
    enableAutoConnect: () => void;
    disableAutoConnect: () => void;
    state: SseStatus
} {

    const store = useStore()
    const {sseStatus} = useSseStatus()
    const jwtRef = computed(() => store.state.jwt)
    const chatterIdRef = computed(() => store.state.chatterId)
    const enableSseWanted = () => store.dispatch(ActionTypes.EnableSseWanted)
    const disableSseWanted = () => store.dispatch(ActionTypes.DisableSseWanted)
    const enableAutoConnect = () => store.dispatch(ActionTypes.EnableSseAutoConnect)
    const disableAutoConnect = () => store.dispatch(ActionTypes.DisableSseAutoConnect)
    const storeSseOpenStatus = (isOpen: boolean) => store.dispatch(ActionTypes.StoreSseOpenStatus, isOpen)
    const storeEventSource = (eventSource: EventSource | undefined) => store.dispatch(ActionTypes.StoreEventSource, eventSource);

    // const switchSseChatter = async (chatterId: ChatterId | undefined, prevChatterId: ChatterId | undefined) => {
    //     console.log("use-sse> switchSseChatter ", prevChatterId, " -> ", chatterId)
    //     if (prevChatterId !== undefined && prevChatterId !== "0") {
    //         store.dispatch(ActionTypes.DeselectConversationId)
    //         await nextTick(() => {
    //             store.dispatch(ActionTypes.ClearChatterState)
    //         })
    //         await nextTick(() => {
    //             disconnect();
    //         })
    //     }
    //     if (chatterId !== undefined && chatterId !== "" && chatterId !== "0") {
    //         await connect();
    //     }
    // }
    // watch(chatterIdRef, switchSseChatter)
    const onJwtChange = async (jwt: string | undefined, previousJwt: string | undefined) => {
        console.log("use-sse> onJwtChange ", previousJwt, " -> ", jwt)
        if (previousJwt !== undefined && previousJwt !== "0") {
            store.dispatch(ActionTypes.DeselectConversationId)
            await nextTick(() => {
                store.dispatch(ActionTypes.ClearChatterState)
            })
            await nextTick(() => {
                disconnect();
            })
        }
        if (jwt !== undefined && jwt !== "" && jwt !== "0") {
            await connect();
        }
    }
    watch(jwtRef, onJwtChange)

    const eventSource = computed(() => store.state.sse.eventSource)

    const disconnect = async () => {
        console.log('use-sse> disconnect');
        if (eventSource.value) {
            disableAutoConnect();
            disableSseWanted();
        }
    }

    const connect = async () => {
        console.log('use-sse> connect');
        if (chatterIdRef.value) {
            enableAutoConnect()
            enableSseWanted()
        }
    }

    const doConnect = async () => {
        const onOpen = async () => {
            console.log('use-sse> SSE opened');
            storeSseOpenStatus(true)
        }
        const onReconnection = async () => {
            console.log('use-sse> Reconnecting SSE...');
            await doDisconnect()
            storeSseOpenStatus(false)
        }
        const onConnectionClosed = async () => {
            console.log('use-sse> SSE connection CLOSED');
            disableSseWanted()
            storeSseOpenStatus(false)
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

        const sseChatKey = await chatApiClient.authent(conversationStatuses);

        try {
            const sseEventSource = SseChatService.openSse(sseChatKey, onOpen, onReconnection, onConnectionClosed);
            storeEventSource(sseEventSource)
            SseChatService.bind(sseEventSource, store);
        } catch (e: any) {
            //
            console.log("⚠️ SseChatService.openSse failed", e)
        }
    }
    const doDisconnect = async () => {
        if (eventSource.value !== undefined) {
            eventSource.value.close();
            storeEventSource(undefined)
        }
    }

    watch(() => sseStatus.sseWanted, async (sseWanted: boolean, prevSseWanted: boolean) => {
        console.log("state.sseWanted", prevSseWanted, " -> ", sseWanted)
        if (sseWanted !== prevSseWanted) {
            if (sseWanted) {
                if (jwtRef.value) {
                    await doConnect()
                }
            } else {
                if (eventSource.value) {
                    await doDisconnect();
                }
            }
        }
    })
    watch(() => store.state.ui.online, (isOnline: boolean, wasOnline: boolean) => {
        console.log("store.state.ui.online", wasOnline, " -> ", isOnline)

        if (isOnline) {
            if (store.state.sse.sseAutoConnect) {
                console.log("Device went ONLINE > re-enable SSE")
                enableSseWanted();
            } else {
                console.log("Device went ONLINE > do nothing")
            }
        } else {
            // device went offline
            console.log("Device went OFFLINE > sseWanted = false")
            disableSseWanted();
        }
    })

    const sseWantedRef = computed(() => sseStatus.sseWanted)
    watch(() => sseStatus.sseOpen, async (sseOpen: boolean, prevSseOpen: boolean) => {
        console.log("state.sseOpen", prevSseOpen, " -> ", sseOpen)
        if (prevSseOpen && !sseOpen && sseWantedRef.value && jwtRef.value) {
            await doConnect()
        }
    })

    return {
        state: sseStatus,
        disconnect,
        enableAutoConnect,
        disableAutoConnect,
    };
}
