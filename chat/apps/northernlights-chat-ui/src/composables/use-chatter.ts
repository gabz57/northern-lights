import {computed, onMounted, reactive, ref, Ref, watch} from "vue";
import {Chatter, ChatterId} from "@/store/state";
import {useStore} from "@/store";

export type ChatterDetails = {
    id?: ChatterId,
    name?: string,
}

export default function useChatter(chatterIdRef: Ref<ChatterId>): {
    details: ChatterDetails;
} {
    const store = useStore()

    const chatterRef = ref<Chatter>()

    const getChatter = () => {
        const chatter = store.getters.getChatterById(chatterIdRef.value);
        if (chatter !== undefined) {
            chatterRef.value = chatter
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