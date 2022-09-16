import {computed} from "vue";
import {useUiStore} from "@/stores/ui";

export default function useConversationCreator() {
    const uiStore = useUiStore()
    const creatingConversation = computed(() => uiStore.creatingConversation)
    const toggleCreatingConversation = () => uiStore.setCreatingConversation(!creatingConversation.value)
    return {
        creatingConversation,
        toggleCreatingConversation
    };
}
