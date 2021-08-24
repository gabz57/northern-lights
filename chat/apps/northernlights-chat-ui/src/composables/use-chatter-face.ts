import {Ref} from "vue";
import {ChatterId} from "@/store/state";

export default function useChatterFace(chatterIdRef: Ref<ChatterId>): {
    face: string;
} {
    const faces = new Map([
        ["1", "❤️"],
        ["2", "😎"],
        ["3", "🐈"],
        ["4", "🍃"],
        ["5", "🥶"],
        ["6", "😈"],
    ])

    return {
        face: faces.get(chatterIdRef.value) || "❓",
    };
}