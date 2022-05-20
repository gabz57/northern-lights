<template>
  <div class="conversation-read-markers">
    <div v-for="[topPosition, localStack] in stackedLocalMarkers" :key="topPosition">
      <ConversationReadMarker class="conversation-read-markers__marker"
                              v-for="(positionedReadMarker) in localStack"
                              :key="positionedReadMarker.chatterId"
                              :style="{ top: `${positionedReadMarker.topPosition}px`, right: `${positionedReadMarker.rightPosition}px`}"
                              :chatter-id="positionedReadMarker.chatterId"/>
    </div>

    <ConversationReadMarker class="conversation-read-markers__marker"
                            v-for="(positionedReadMarker) in localMarkers"
                            :key="positionedReadMarker.chatterId"
                            :style="{ top: `${positionedReadMarker.topPosition}px`, right: `${positionedReadMarker.rightPosition}px`}"
                            :chatter-id="positionedReadMarker.chatterId"/>
  </div>
</template>

<script lang="ts">
import {defineComponent, PropType, ref, toRefs, watch} from "vue";
import ConversationReadMarker from "@/components/chat/ConversationReadMarker.vue";
import {gsap} from "gsap";
import {ChatterId} from "@/store/state";
import {PositionedReadMarker, PositionedReadMarkerWithXPos} from "@/composables/use-conversation-read-markers";

export default defineComponent({
  name: "ConversationReadMarkers",
  components: {ConversationReadMarker},
  props: {
    markers: {
      type: Object as PropType<PositionedReadMarker[]>,
      required: true
    }
  },
  setup(props) {
    const {markers} = toRefs(props)

    const localMarkers = ref<PositionedReadMarkerWithXPos[]>([])
    const stackedLocalMarkers = ref<Map<number, PositionedReadMarkerWithXPos[]>>(new Map<number, PositionedReadMarkerWithXPos[]>())
    const timelinePerChatterId = ref<Map<ChatterId, gsap.core.Timeline>>(new Map<ChatterId, gsap.core.Timeline>())

    const MOVE_DOWN_DURATION = .5;
    const MOVE_LEFT_DURATION = .05;
    const MOVE_RIGHT_DURATION = 1.5;
    const DEFAULT_RIGHT_POSITION_IN_PX = 20;
    const MARKER_SPACING_IN_PX = 7;

    const unstack = (localMarker: PositionedReadMarker) => {
      Array.from(stackedLocalMarkers.value.values())
          .filter(localMarkersStack => localMarkersStack.some(localMarkersElem => localMarkersElem.chatterId === localMarker.chatterId))
          .forEach(localStack => {
            const idxToRemove = localStack.findIndex(elem => elem.chatterId === localMarker.chatterId);
            if (idxToRemove > -1) {
              localStack.splice(idxToRemove, 1);
              for (let offset = idxToRemove; offset < localStack.length; offset++) {
                gsap.to(localStack[offset], {
                  duration: MOVE_RIGHT_DURATION,
                  rightPosition: DEFAULT_RIGHT_POSITION_IN_PX + offset * MARKER_SPACING_IN_PX
                })
              }
            }
          })
    }
    const stack = (localMarker: PositionedReadMarkerWithXPos) => {
      const idxToRemove = localMarkers.value.findIndex(elem => elem.chatterId === localMarker.chatterId);
      if (idxToRemove > -1) localMarkers.value.splice(idxToRemove, 1);

      let offset = 0;
      const localStack = stackedLocalMarkers.value.get(localMarker.topPosition);
      if (localStack !== undefined) {
        offset = localStack.length
        localStack.push(localMarker)
      } else {
        stackedLocalMarkers.value.set(localMarker.topPosition, [localMarker])
      }

      gsap.to(localMarker, {
        duration: MOVE_LEFT_DURATION,
        rightPosition: DEFAULT_RIGHT_POSITION_IN_PX + offset * MARKER_SPACING_IN_PX
      })
    }
    const updateLocalMarker = (newMarker: PositionedReadMarker, oldMarker: PositionedReadMarker | undefined) => {
      timelinePerChatterId.value.get(newMarker.chatterId)?.progress(1);

      localMarkers.value.push(oldMarker === undefined
          ? {...newMarker, rightPosition: DEFAULT_RIGHT_POSITION_IN_PX}
          : {...oldMarker, rightPosition: DEFAULT_RIGHT_POSITION_IN_PX}
      )
      const localMarker = localMarkers.value[localMarkers.value.length - 1];

      timelinePerChatterId.value.set(newMarker.chatterId, gsap.timeline({
        onStart: () => unstack(localMarker),
        onComplete: () => stack(localMarker)
      }).to(localMarker, {duration: MOVE_DOWN_DURATION, topPosition: newMarker.topPosition}))
    }

    watch(markers, (newMarkers, prevMarkers) => {
      if (newMarkers.length === 0) {
        stackedLocalMarkers.value.clear()
      } else if (prevMarkers.length === 0) {
        newMarkers.forEach(newMarker => updateLocalMarker(newMarker, undefined))
      } else {
        const markerWithTopPositionChange = (prevMarker: PositionedReadMarker, newMarker: PositionedReadMarker) =>
            prevMarker.chatterId === newMarker.chatterId
            && prevMarker.topPosition !== newMarker.topPosition;

        newMarkers.filter(newMarker =>
            prevMarkers.find(prevMarker => markerWithTopPositionChange(prevMarker, newMarker))
            || !prevMarkers.find(prevMarker => prevMarker.chatterId === newMarker.chatterId)
        ).forEach(newMarker => updateLocalMarker(newMarker, prevMarkers.find(prevMarker => prevMarker.chatterId === newMarker.chatterId)))

        prevMarkers.filter(prevMarker => newMarkers.find(newMarker => prevMarker.chatterId === newMarker.chatterId) === undefined)
            .forEach(missingMarker => unstack(missingMarker))
      }
    })

    return {
      localMarkers,
      stackedLocalMarkers
    }
  }
});

</script>

<style scoped lang="scss">

.conversation-read-markers {
  position: relative;

  &__marker {
    position: absolute;
    right: 20px;
  }
}
</style>
