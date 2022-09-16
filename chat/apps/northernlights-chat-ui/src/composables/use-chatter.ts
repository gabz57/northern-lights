import type { Ref } from "vue";
import { computed, onMounted, reactive, ref, watch } from "vue";
import type { Chatter, ChatterId } from "@/domain/model";
import { useChattersStore } from "@/stores/chatter";

export type ChatterDetails = {
  id?: ChatterId;
  name?: string;
};

export default function useChatter(chatterIdRef: Ref<ChatterId>): {
  details: ChatterDetails;
} {
  const chattersStore = useChattersStore();

  const chatterRef = ref<Chatter>();

  const getChatter = () => {
    if (chatterIdRef.value) {
      const chatter = chattersStore.getChatterById(chatterIdRef.value);
      if (chatter !== undefined) {
        chatterRef.value = chatter;
      }
    } else {
      chatterRef.value = undefined;
    }
  };

  onMounted(getChatter);
  watch(chatterIdRef, getChatter);

  const details: ChatterDetails = reactive({
    id: computed(() => chatterRef.value?.id),
    name: computed(() => chatterRef.value?.name),
  });

  return {
    details,
  };
}
