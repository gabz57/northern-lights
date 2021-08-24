import {computed} from "vue";
import {ChatterId, Profile} from "@/store/state";
import {useStore} from "@/store";
import {ActionTypes} from "@/store/actions";

export default function useProfile() {
    const store = useStore()

    const setChatterId = (chatterId: ChatterId) => store.dispatch(ActionTypes.SetChatterId, {chatterId})
    const editingProfile = computed(() => store.state.ui.editingProfile)
    const profile = computed<Profile | undefined>(() => store.state.profile)

    const toggleEditingProfile = () => store.dispatch(ActionTypes.SetEditingProfile, !editingProfile.value)

    return {
        profile,
        setChatterId,
        toggleEditingProfile,
    };
}