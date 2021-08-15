import {computed, reactive} from "vue";
import {useStore} from "@/store";

type SseStatus = {
    sseAutoConnect: boolean,
    sseWanted: boolean,
    sseOpen: boolean,
}

export default function useSseState() {
    const store = useStore()
    const state: SseStatus = reactive({
        sseAutoConnect: computed(() => store.state.sse.sseAutoConnect),
        sseWanted: computed(() => store.state.sse.sseWanted),
        sseOpen: computed(() => store.state.sse.sseOpen),
    })

    return {
        state,
    };
}
