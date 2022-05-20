import {computed, onMounted, ref, Ref, watch} from 'vue';
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
    const store = useStore()

    const sendMessage = (message: string) => {
        store.dispatch(ActionTypes.SendMessage, {
            chatterId: store.state.chatterId || "",
            conversationId: conversationIdRef.value,
            message
        })
    }
    const markAsRead = (conversationDataId: ConversationDataId) => {
        if (store.state.ui.visible) {
            store.dispatch(ActionTypes.MarkAsRead, {
                chatterId: store.state.chatterId || "",
                conversationId: conversationIdRef.value,
                conversationDataId
            })
        }
    }
    const messages = ref<ConversationDataWithMarkers[]>([])
    const readMarkers = ref<ReadMarkers>(new Map<ChatterId, ConversationDataId>())

    const conversationRef = computed<Conversation>(() => store.getters.getConversationById(conversationIdRef.value))
    onMounted(() => loadConversationContent(conversationRef.value))
    watch(conversationRef, (conversation) => loadConversationContent(conversation), {deep: true})
    const loadConversationContent = (conversation: Conversation) => {
        messages.value = withReadMarkers(conversation.data, conversation.readMarkers)
        readMarkers.value = conversation.readMarkers
    };

    const withReadMarkers = (data: ConversationData[], markers: ReadMarkers): ConversationDataWithMarkers[] => {
        const reversed: ReadMarkersReversed = reverse(markers);
        const marker: ConversationDataId | undefined = markers.get(store.state.chatterId || "");
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
