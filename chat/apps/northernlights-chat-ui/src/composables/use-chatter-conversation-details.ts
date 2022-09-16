import {computed, reactive, Ref} from 'vue';
import {ChatterId, Conversation, ConversationDataId, ConversationId,} from "@/domain/model";
import {useConversationsStore} from "@/stores/conversations";
import {useUserStore} from "@/stores/user";

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
    const userStore = useUserStore()
    const conversationsStore = useConversationsStore()

    const conversationRef = computed(() => conversationsStore.getPrivateConversationWithChatterId(chatterIdRef.value))

    const nbUnreadMsgs = (conversation: Conversation): number => {
        const lastConversationDataIdSeenByChatter: ConversationDataId | undefined = conversation.readMarkers.get(userStore.chatterId || "");
        const msgs = conversation.data;
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
    };

    return {
        details: reactive({
            id: computed(() => conversationRef.value?.id),
            createdBy: computed(() => conversationRef.value?.creator),
            createdAt: computed(() => conversationRef.value?.createdAt),
            nbUnreadMessages: computed(() => conversationRef.value ? nbUnreadMsgs(conversationRef.value) : 0),
            participants: computed(() => conversationRef.value?.participants || []),
        })
    };
}
