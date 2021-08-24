/* eslint-disable no-debugger */

import {computed, Ref} from 'vue';
import {ChatterId,} from "@/store/state";
import {useStore} from "@/store";
import {ActionTypes} from "@/store/actions";

export default function useChatterConversationOpener(chatterIdRef: Ref<ChatterId>): {
    openConversationWith: () => void;
} {
    const store = useStore();
    const createDialogueConversation = (chatterId: ChatterId) => {
        if (chatterId === store.state.chatterId) {
            return
        }
        store.dispatch(ActionTypes.CreateConversation, {
            chatterId: store.state.chatterId || "",
            name: "",
            participants: [chatterId],
            dialogue: true,
        })
    }

    const conversationId = computed(() => {
        return Array.from(store.state.conversations.values()).find(c => {
            return c.dialogue
                && store.state.chatterId !== undefined && c.participants.includes(store.state.chatterId)
                && c.participants.includes(chatterIdRef.value)
        })?.id
    })

    const openConversationWith = (): void => {
        if (chatterIdRef.value === store.state.chatterId) {
            return
        }
        if (conversationId.value !== undefined) {
            store.dispatch(ActionTypes.SetSelectedConversationId, conversationId.value)
        } else {
            createDialogueConversation(chatterIdRef.value)
        }
    }


    return {
        openConversationWith
    };
}