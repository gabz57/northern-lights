import {computed} from "vue";
import {useUserStore} from "@/stores/user";
import {useChattersStore} from "@/stores/chatter";

export default function useChatters() {
    const userStore = useUserStore()
    const chattersStore = useChattersStore()

    const chatterIds = computed(() => Array.from(chattersStore.chatters.values())
        .filter(chatter => chatter.id !== userStore.chatterId)
        .map(chatter => chatter.id))

    return {
        chatterIds,
    };
}
