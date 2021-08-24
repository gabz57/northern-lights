/* eslint-disable no-debugger */

import {computed, reactive, Ref} from 'vue';
import {ChatterId, Conversation, ConversationDataId, ConversationId,} from "@/store/state";
import {useStore} from "@/store";

export type ConversationDetails = {
    id?: ConversationId,
    name?: string,
    dialogue?: boolean,
    createdBy?: ChatterId,
    createdAt?: number,
    nbUnreadMessages: number,
    participants: ChatterId[]
}

export default function useConversationDetails(conversationIdRef: Ref<ConversationId>): {
    details: ConversationDetails;
} {
    const store = useStore()

    const getOtherChatter = (conversation: Conversation) => {
        const selfChatterId = store.state.chatterId as NonNullable<typeof store.state.chatterId>;
        const otherChatterId = Array.from(conversation.participants.values())
            .find(chatterId => selfChatterId !== chatterId);
        let otherChatter
        if (otherChatterId !== undefined) {
            otherChatter = store.getters.getChatterById(otherChatterId);
        }
        return otherChatter;
    }

    const conversationRef = computed<Conversation>(() => store.getters.getConversationById(conversationIdRef.value))

    function conversationName(conversation: Conversation): string {
        return conversation.dialogue
            ? getOtherChatter(conversation)?.name || ""
            : conversation.name
    }

    const details: ConversationDetails = reactive({
        id: computed(() => conversationRef.value.id),
        name: computed(() => conversationName(conversationRef.value)),
        dialogue: computed(() => conversationRef.value.dialogue),
        createdBy: computed(() => conversationRef.value.creator),
        createdAt: computed(() => conversationRef.value.createdAt),
        nbUnreadMessages: computed(() => {
            const lastConversationDataIdSeenByChatter: ConversationDataId | undefined = conversationRef.value.readMarkers.get(store.state.chatterId || "");
            const msgs = conversationRef.value.data;
            if (lastConversationDataIdSeenByChatter === undefined) {
                return msgs.length
            }

            let unreadCount = 0
            let i = msgs.length - 1
            for (; i >= 0; i--) {
                if (msgs[i].id === lastConversationDataIdSeenByChatter) {
                    break;
                }
                unreadCount++
            }
            return unreadCount
        }),
        participants: computed(() => conversationRef.value?.participants || []),
    })

    return {
        details,
    };
}