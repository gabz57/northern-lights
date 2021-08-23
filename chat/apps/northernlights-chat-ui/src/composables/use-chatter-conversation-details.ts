/* eslint-disable no-debugger */

import {computed, reactive, Ref} from 'vue';
import {ChatterId, ConversationDataId, ConversationId,} from "@/store/state";
import {useStore} from "@/store";

export type ChatterConversationDetails = {
    id?: ConversationId,
    // name?: string,
    createdBy?: ChatterId,
    createdAt?: number,
    nbUnreadMessages: number,
    participants: ChatterId[]
}

export default function useChatterConversationDetails(chatterIdRef: Ref<ChatterId>): {
    details: ChatterConversationDetails;
} {
    const store = useStore()

    const conversationRef = computed(() => store.getters.getDirectConversationByChatterId(chatterIdRef.value))

    const details: ChatterConversationDetails = reactive({
        id: computed(() => conversationRef.value?.id),
        // name: computed(() => conversationRef.value?.name),
        createdBy: computed(() => conversationRef.value?.creator),
        createdAt: computed(() => conversationRef.value?.createdAt),
        nbUnreadMessages: computed(() => {
            const lastConversationDataIdSeenByChatter: ConversationDataId | undefined = conversationRef.value?.readMarkers.get(store.state.chatterId || "");
            const msgs = conversationRef.value?.data || [];
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