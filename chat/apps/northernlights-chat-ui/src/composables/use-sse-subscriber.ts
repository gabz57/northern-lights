import {computed} from "vue";
import {ChatterId} from "@/store/state";
import {useStore} from "@/store";
import {ActionTypes} from "@/store/actions";
import {userApiClient} from "@/services/UserApiClient";

export default function useSseSubscriber(): {
    subscribe: () => Promise<void>;
} {

    const store = useStore()
    const chatterIdRef = computed(() => store.state.chatterId)
    const setChatterId = (chatterId: ChatterId) => store.dispatch(ActionTypes.SetChatterId, {chatterId})

    const subscribe = async () => {
        console.log('use-sse-subscriber> subscribe ?');
        if (!chatterIdRef.value || chatterIdRef.value === "0") {
            console.log('use-sse-subscriber> subscribe');
            const chatterId = await userApiClient.subscribe().catch(e => console.log(e));
            if (chatterId != null) {
                setChatterId(chatterId)
            }
        } else {
            console.log('use-sse-subscriber> skip', chatterIdRef.value);
        }
    }

    return {
        subscribe,
    };
}
