import {useStore} from "@/store";
import {computed} from "vue";
import {ActionTypes} from "@/store/actions";

export default function useConversationCreator() {
    const store = useStore()
    const creatingConversation = computed(() => store.state.ui.creatingConversation)
    const toggleCreatingConversation = () => {
        store.dispatch(ActionTypes.SetCreatingConversation, !creatingConversation.value)
    }
    return {
        creatingConversation,
        toggleCreatingConversation
    };
}
