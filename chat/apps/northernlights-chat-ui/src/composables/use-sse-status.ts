import {computed, reactive} from "vue";
import {useSseStore} from "@/stores/sse";

export type SseStatus = {
    sseAutoConnect: boolean,
    sseWanted: boolean,
    sseOpen: boolean,
}

export default function useSseStatus() {
    const sseStore = useSseStore()
    return {
        sseStatus: reactive({
            sseAutoConnect: computed(() => sseStore.sseAutoConnect),
            sseWanted: computed(() => sseStore.sseWanted),
            sseOpen: computed(() => sseStore.sseOpen),
        }),
    };
}
