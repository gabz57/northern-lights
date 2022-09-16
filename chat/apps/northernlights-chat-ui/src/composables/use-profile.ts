import {computed} from "vue";
import {useUserStore} from "@/stores/user";
import {useUiStore} from "@/stores/ui";

export default function useProfile() {
    const userStore = useUserStore()
    const uiStore = useUiStore()
    const editingProfile = computed(() => uiStore.editingProfile)

    return {
        profile: computed(() => userStore.profile),
        chatterId: computed(() => userStore.chatterId),
        toggleEditingProfile: () => uiStore.setEditingProfile(!editingProfile.value),
    };
}
