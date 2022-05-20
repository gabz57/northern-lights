import {computed} from "vue";
import {useStore} from "@/store";
import {ActionTypes} from "@/store/actions";

export default function useProfile() {
    const store = useStore()
    const editingProfile = computed(() => store.state.ui.editingProfile)
    const toggleEditingProfile = () => store.dispatch(ActionTypes.SetEditingProfile, !editingProfile.value)

    return {
        profile: computed(() => store.state.profile),
        chatterId: computed(() => store.state.chatterId),
        toggleEditingProfile,
    };
}
