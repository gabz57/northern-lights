/* eslint-disable no-debugger */

import {computed, onMounted, Ref, ref, watch, ComputedRef} from 'vue';
import {
    ChatterId,
    Conversation,
    ConversationData,
    ConversationDataId,
    ConversationId,
    ReadMarkers,
} from "@/store/state";
import {useStore} from "@/store";
import {ActionTypes} from "@/store/actions";
import useConversationDetails, {ConversationDetails} from "@/composables/use-conversation-details";

type ReadMarkersReversed = Map<ConversationDataId, ChatterId[]>;

export type Markers = {
    index: number,
    readBy: ChatterId[],
    onVisible: () => void
    watchVisible: boolean
}

export type ConversationDataWithMarkers = ConversationData & Markers

export default function useConversation(conversationIdRef: Ref<ConversationId>): {
    messages: Ref<ConversationDataWithMarkers[]>;
    details: ConversationDetails;
    sendMessage: (message: string) => void;
    markAsRead: (conversationDataId: ConversationDataId) => void;
    readMarkers: Ref<ReadMarkers>
} {
    const store = useStore()

    const conversationRef = ref<Conversation>()

    const reverse = (readMarkers: ReadMarkers): ReadMarkersReversed => {
        const reversed = new Map<ConversationDataId, ChatterId[]>()
        readMarkers.forEach((conversationDataId: ConversationDataId, chatterId: ChatterId) => {
            const chatterIds = reversed.get(conversationDataId) || []
            chatterIds.push(chatterId)
            reversed.set(conversationDataId, chatterIds)
        })
        return reversed;
    }
    const lastEmittedReadMarkerRef = ref<ConversationDataId | undefined>()
    const maxConversationDataId = (a: ConversationDataId | undefined, b: ConversationDataId | undefined): ConversationDataId | undefined => {
        if (a === undefined) {
            return b
        }
        if (b === undefined) {
            return a
        }
        return a > b ? a : b
    }

    const withReadMarkers = (data: ConversationData[], readMarkers: ReadMarkers): ConversationDataWithMarkers[] => {
        const reversed: ReadMarkersReversed = reverse(readMarkers);
        const lastConversationDataIdSeenByChatter: ConversationDataId | undefined = maxConversationDataId(lastEmittedReadMarkerRef.value, readMarkers.get(store.state.chatterId || ""));
        let index = 0
        return data.map(conversationData => {
            const watchVisible = !lastConversationDataIdSeenByChatter || conversationData.id > lastConversationDataIdSeenByChatter;
            return {
                ...conversationData,
                readBy: reversed.get(conversationData.id) || [],
                watchVisible: watchVisible,
                onVisible: () => {
                    watchVisible && markAsRead(conversationData.id)
                },
                index: index++
            }
        })
    }

    const getConversation = () => {
        const conversation = store.getters.getConversationById(conversationIdRef.value);
        if (conversation !== undefined) {
            conversationRef.value = conversation
            lastEmittedReadMarkerRef.value = undefined
        }
    }

    onMounted(getConversation)
    watch(conversationIdRef, getConversation)

    const sendMessage = (message: string) => {
        store.dispatch(ActionTypes.SendMessage, {
            chatterId: store.state.chatterId || "",
            conversationId: conversationIdRef.value,
            message
        })
    }
    const markAsRead = (conversationDataId: ConversationDataId) => {
        if (store.state.ui.visible) {
            lastEmittedReadMarkerRef.value = conversationDataId
            store.dispatch(ActionTypes.MarkAsRead, {
                chatterId: store.state.chatterId || "",
                conversationId: conversationIdRef.value,
                conversationDataId
            })
        }
    }

    const messages = computed<ConversationDataWithMarkers[]>(() =>
        withReadMarkers(
            conversationRef.value?.data || [],
            conversationRef.value?.readMarkers || new Map<ChatterId, ConversationDataId>()
        )
    )
    const readMarkers = computed(() => conversationRef.value?.readMarkers || new Map<ChatterId, ConversationDataId>())

    return {
        details: useConversationDetails(conversationIdRef).details,
        sendMessage,
        markAsRead,
        messages,
        readMarkers,
    };
}