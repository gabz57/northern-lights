import {computed, reactive} from "vue";
import {useStore} from "@/store";

export type SseStatus = {
    sseAutoConnect: boolean,
    sseWanted: boolean,
    sseOpen: boolean,
}

export default function useSseStatus() {
    const store = useStore()
    return {
        sseStatus: reactive({
            sseAutoConnect: computed(() => store.state.sse.sseAutoConnect),
            sseWanted: computed(() => store.state.sse.sseWanted),
            sseOpen: computed(() => store.state.sse.sseOpen),
        }),
    };
}
