import {nextTick, Ref, ref} from 'vue';
import {ChatterId, ReadMarkers,} from "@/store/state";
import {ComponentPublicInstance} from "vue";
import {ConversationDataWithMarkers} from "@/composables/use-conversation";

export type PositionedReadMarker = {
    chatterId: ChatterId
    topPosition: number
}

export type PositionedReadMarkerWithXPos = PositionedReadMarker & { rightPosition: number }

export default function useConversationReadMarkers(readMarkers: Ref<ReadMarkers>, messages: Ref<ConversationDataWithMarkers[]>): {
    divs: Ref<Element[]>
    clearDivs: () => void;
    positionedReadMarkers: Ref<PositionedReadMarker[]>
    updateReadMarkers: () => void;
} {
    const positionedReadMarkers = ref<PositionedReadMarker[]>([])
    const updateReadMarkers = async () => {
        await nextTick(() => {
            const arr: PositionedReadMarker[] = []
            if (readMarkers.value) {
                readMarkers.value.forEach((conversationDataId, chatterId) => {
                    const indexedMessage = messages.value.find(m => m.id === conversationDataId);
                    if (indexedMessage !== undefined) {
                        const div = divs.value[indexedMessage.index] as ComponentPublicInstance
                        if (div !== undefined && div !== null) {
                            arr.push({
                                chatterId,
                                topPosition: div.$el.offsetTop - 4
                            })
                        }
                    }
                })
                positionedReadMarkers.value = arr
            }
        })
    }

    const divs = ref([])
    // make sure to reset the refs before each update
    const clearDivs = () => {
        divs.value = []
    }
    return {
        divs,
        clearDivs,
        positionedReadMarkers,
        updateReadMarkers
    };
}
