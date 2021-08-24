/* eslint-disable no-debugger */

import {useStore} from "@/store";
import {computed} from "vue";

export default function useChatters() {
    const store = useStore()

    const chatterIds = computed(() => Array.from(store.state.chatters.values())
        .filter(chatter => chatter.id !== store.state.chatterId)
        .map(chatter => chatter.id))

    return {
        chatterIds,
    };
}