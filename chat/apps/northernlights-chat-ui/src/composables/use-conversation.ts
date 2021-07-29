/* eslint-disable no-debugger */

import {onMounted, Ref, ref, watch} from 'vue';
import {ConversationData, ConversationDataId, ConversationId} from "@/store/state";
import {useStore} from "@/store";
import {ActionTypes} from "@/store/actions";

export default function useConversation(conversationIdRef: Ref<ConversationId>): {
    messages: Ref<ConversationData[]>;
    sendMessage: (message: string) => void;
    markAsRead: (conversationDataId: ConversationDataId) => void
} {
    const store = useStore()

    const messages = ref<ConversationData[]>([])
    const getConversation = () => {
        messages.value = store.getters.getConversationById(conversationIdRef.value)?.data || []
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

    return {
        messages,
        sendMessage,
        markAsRead
    };
}