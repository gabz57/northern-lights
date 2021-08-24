/* eslint-disable no-debugger */

import {useStore} from "@/store";
import {computed} from "vue";
import {ConversationId} from "@/store/state";
import {ActionTypes} from "@/store/actions";

export default function useConversations(dialogues: boolean | undefined) {
    const store = useStore()
    const conversationIds = computed(() => Array.from(store.state.conversations.values())
        .filter(conversation => dialogues === undefined || conversation.dialogue === dialogues)
        .map(conversation => conversation.id))
    const selectConversation = (conversationId: ConversationId): void => {
        store.dispatch(ActionTypes.SetSelectedConversationId, conversationId)
    }

    return {
        conversationIds,
        selectConversation,
    };
}