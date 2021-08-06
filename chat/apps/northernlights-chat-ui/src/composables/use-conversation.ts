/* eslint-disable no-debugger */

import {computed, onMounted, Ref, ref, watch} from 'vue';
import {
    ChatterId, Conversation,
    ConversationData,
    ConversationDataId,
    ConversationId,
    ReadMarkers,
} from "@/store/state";
import {useStore} from "@/store";
import {ActionTypes} from "@/store/actions";
import useConversationDetails from "@/composables/use-conversation-details";

export type ConversationDetails = {
    name?: string,
    createdBy?: ChatterId,
    createdAt?: number,
    nbUnreadMessages: number,
}

export type ReadMarkersReversed = Map<ConversationDataId, ChatterId[]>;

export type ConversationDataWithMarkers = ConversationData & {
    readBy: ChatterId[],
    onVisible: () => void
    watchVisible: boolean
}

export default function useConversation(conversationIdRef: Ref<ConversationId>): {
    messages: Ref<ConversationDataWithMarkers[]>;
    details: ConversationDetails;
    sendMessage: (message: string) => void;
    markAsRead: (conversationDataId: ConversationDataId) => void
} {
    const store = useStore()

    const conversationRef= ref<Conversation>()

    const reverse = (readMarkers: ReadMarkers): ReadMarkersReversed => {
        const reversed = new Map<ConversationDataId, ChatterId[]>()
        readMarkers.forEach((conversationDataId: ConversationDataId, chatterId: ChatterId) => {
            const chatterIds = reversed.get(conversationDataId) || []
            chatterIds.push(chatterId)
            reversed.set(conversationDataId, chatterIds)
        })
        return reversed;
    }
    const withReadMarkers = (data: ConversationData[], readMarkers: ReadMarkers): ConversationDataWithMarkers[] => {
        const reversed: ReadMarkersReversed = reverse(readMarkers);

        const lastConversationDataIdSeenByChatter: ConversationDataId | undefined = readMarkers.get(store.state.chatterId || "");

        return data.map(conversationData => {
            const watchVisible = !lastConversationDataIdSeenByChatter || conversationData.id > lastConversationDataIdSeenByChatter;
            return {
                ...conversationData,
                readBy: reversed.get(conversationData.id) || [],
                watchVisible: watchVisible,
                onVisible: () => {
                    markAsRead(conversationData.id)
                },
            }
        })
    }

    const getConversation = () => {
        const conversation = store.getters.getConversationById(conversationIdRef.value);
        if (conversation !== undefined) {
            conversationRef.value = conversation
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
        store.dispatch(ActionTypes.MarkAsRead, {
            chatterId: store.state.chatterId || "",
            conversationId: conversationIdRef.value,
            conversationDataId
        })
    }

    const messages = computed<ConversationDataWithMarkers[]>(() =>
        withReadMarkers(
            conversationRef.value?.data || [],
            conversationRef.value?.readMarkers || new Map<ChatterId, ConversationDataId>()
        )
    )

    return {
        details: useConversationDetails(conversationIdRef).details,
        messages,
        sendMessage,
        markAsRead
    };
}