import {Ref} from "vue";
import {ChatterId} from "@/domain/model";

export default function useChatterFace(chatterIdRef: Ref<ChatterId>): {
    face: string;
} {
    const faces = new Map([
        ["3551ba8c-ef09-4459-9c4d-39c896b1ce6d", "❤️"],
        ["bb0188da-606a-4198-894b-56ba002721c4", "😎"],
        ["804d41f7-6782-464b-a196-2907846b9998", "🐈"],
        ["1be26bb1-f5c8-418d-a0a2-b432b30c5bf3", "🍃"],
        ["5", "🥶"],
        ["6", "😈"],
    ])

    return {
        face: faces.get(chatterIdRef.value) || "❓",
    };
}
