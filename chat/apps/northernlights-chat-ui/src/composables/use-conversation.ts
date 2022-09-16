import {computed, onMounted, ref, Ref, watch} from 'vue';
import {
    ChatterId,
    Conversation,
    ConversationData,
    ConversationDataId,
    ConversationId,
    ReadMarkers,
} from "@/domain/model";
import useConversationDetails, {ConversationDetails} from "@/composables/use-conversation-details";
import {useConversationsStore} from "@/stores/conversations";
import {useUserStore} from "@/stores/user";
import {useUiStore} from "@/stores/ui";

type ReadMarkersReversed = Map<ConversationDataId, ChatterId[]>;

export type Markers = {
    index: number,
    readBy: ChatterId[],
    onVisible: () => void
    watchVisible: boolean
}

export type ConversationDataWithMarkers = ConversationData & Markers

const reverse = (readMarkers: ReadMarkers): ReadMarkersReversed => {
    const reversed = new Map<ConversationDataId, ChatterId[]>()
    readMarkers.forEach((conversationDataId: ConversationDataId, chatterId: ChatterId) => {
        const chatterIds = reversed.get(conversationDataId) || []
        chatterIds.push(chatterId)
        reversed.set(conversationDataId, chatterIds)
    })
    return reversed;
}

export default function useConversation(conversationIdRef: Ref<ConversationId>): {
    details: ConversationDetails;
    sendMessage: (message: string) => void;
    markAsRead: (conversationDataId: ConversationDataId) => void;
    messages: Ref<ConversationDataWithMarkers[]>;
    readMarkers: Ref<ReadMarkers>;
} {
    const conversationsStore = useConversationsStore()
    const userStore = useUserStore()
    const uiStore = useUiStore()

    const sendMessage = (message: string) => {
        conversationsStore.sendMessage(
            userStore.chatterId || "",
            conversationIdRef.value,
            message
        )
    }
    const markAsRead = (conversationDataId: ConversationDataId) => {
        if (uiStore.visible) {
            conversationsStore.markAsRead(
                userStore.chatterId || "",
                conversationIdRef.value,
                conversationDataId
            )
        }
    }
    const messages = ref<ConversationDataWithMarkers[]>([])
    const readMarkers = ref<ReadMarkers>(new Map<ChatterId, ConversationDataId>())

    const conversationRef = computed<Conversation>(() => conversationsStore.getConversationById(conversationIdRef.value))
    onMounted(() => loadConversationContent(conversationRef.value))
    watch(conversationRef, (conversation) => loadConversationContent(conversation), {deep: true})
    const loadConversationContent = (conversation: Conversation) => {
        messages.value = withReadMarkers(conversation.data, conversation.readMarkers)
        readMarkers.value = conversation.readMarkers
    };

    const withReadMarkers = (data: ConversationData[], markers: ReadMarkers): ConversationDataWithMarkers[] => {
        const reversed: ReadMarkersReversed = reverse(markers);
        const marker: ConversationDataId | undefined = markers.get(userStore.chatterId || "");
        let index = 0
        return data.map(conversationData => {
            const watchVisible = !marker || conversationData.id > marker;
            return {
                ...conversationData,
                readBy: reversed.get(conversationData.id) || [],
                watchVisible,
                onVisible: () => {
                    watchVisible && markAsRead(conversationData.id)
                },
                index: index++
            }
        })
    }

    return {
        ...useConversationDetails(conversationIdRef),
        sendMessage,
        markAsRead,
        messages,
        readMarkers
    };
}
