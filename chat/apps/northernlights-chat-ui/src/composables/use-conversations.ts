import {computed} from "vue";
import {ConversationId} from "@/domain/model";
import {useUiStore} from "@/stores/ui";
import {useConversationsStore} from "@/stores/conversations";

export default function useConversations(dialogues: boolean | undefined) {
    const conversationsStore = useConversationsStore()
    const uiStore = useUiStore()
    const conversationIds = computed(() => Array.from(conversationsStore.conversations.values())
        .filter(conversation => dialogues === undefined || conversation.dialogue === dialogues)
        .map(conversation => conversation.id))
    const selectConversation = (conversationId: ConversationId) => uiStore.setSelectedConversationId(conversationId)
    return {
        conversationIds,
        selectConversation,
    };
}
