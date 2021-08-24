import {computed, onMounted, reactive, ref, Ref, watch} from "vue";
import {Chatter, ChatterId} from "@/store/state";
import {useStore} from "@/store";

export type ChatterDetails = {
    id?: ChatterId,
    name?: string,
}

export default function useChatter(chatterIdRef: Ref<ChatterId | undefined>): {
    details: ChatterDetails;
} {
    const store = useStore()

    const chatterRef = ref<Chatter>()

    const getChatter = () => {
        if (chatterIdRef.value) {
            const chatter = store.getters.getChatterById(chatterIdRef.value);
            if (chatter !== undefined) {
                chatterRef.value = chatter
            }
        } else {
            chatterRef.value = undefined
        }
    }

    onMounted(getChatter)
    watch(chatterIdRef, getChatter)

    const details: ChatterDetails = reactive({
        id: computed(() => chatterRef.value?.id),
        name: computed(() => chatterRef.value?.name),
    })

    return {
        details,
    };
}