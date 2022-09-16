import {defineStore} from "pinia";
import {ConversationId} from "@/domain/model";
import {ref} from "vue";

export const useUiStore = defineStore("ui", () => {
    // state
    const online = ref<boolean>(true)
    const visible = ref<boolean>(true)
    const selectedConversationId = ref<ConversationId>()
    const editingProfile = ref<boolean>(false)
    const creatingConversation = ref<boolean>(false)

    // actions
    function updateNavigatorOnlineStatus(isOnline: boolean) {
        console.log("uiStore.updateNavigatorOnlineStatus", isOnline)
        online.value = isOnline
    }

    function updateChatVisibility(isVisible: boolean) {
        console.log("uiStore.updateChatVisibility", isVisible)
        visible.value = isVisible
    }

    function setSelectedConversationId(conversationId: ConversationId) {
        selectedConversationId.value = conversationId
        editingProfile.value = false
        creatingConversation.value = false
    }

    function deselectConversationId() {
        selectedConversationId.value = undefined
    }

    function setEditingProfile(enabled: boolean) {
        editingProfile.value = enabled
        creatingConversation.value = false
    }

    function setCreatingConversation(enabled: boolean) {
        editingProfile.value = false
        creatingConversation.value = enabled
    }

    return {
        online, visible, selectedConversationId, editingProfile, creatingConversation,
        updateNavigatorOnlineStatus,
        updateChatVisibility,
        setSelectedConversationId,
        deselectConversationId,
        setEditingProfile,
        setCreatingConversation
    }
});
