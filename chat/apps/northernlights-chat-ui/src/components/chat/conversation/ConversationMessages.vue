<template>
  <div class="conversation-messages">
    <div
      v-if="!shouldStickToBottom"
      class="conversation-messages__arrow"
      :class="{
        'conversation-messages__arrow--with-new-message': hasNewMessage,
      }"
    >
      <button @click="scrollToBottom">⬇</button>
    </div>
    <div
      class="conversation-messages__wrapper"
      ref="messagesContainer"
      @scroll.passive="handleScroll"
    >
      <ConversationReadMarkers :markers="positionedReadMarkers" />
      <div
        v-if="
          !!dailyMessagePacksPerDay &&
          !!dailyMessagePacksPerDay[0] &&
          dailyMessagePacksPerDay[0][0][0].type === 'CREATION'
        "
      >
        <span style="color: #8599ad">
          This is the beginning of the conversation
        </span>
      </div>
      <div
        class="conversation-messages__per-day"
        v-for="(dailyMessagePacksOfDay, index) in dailyMessagePacksPerDay"
        :key="index"
      >
        <div class="separator" v-if="dailyMessagePacksOfDay[0][0].type !== 'CREATION'">
          {{ $filters.date(dailyMessagePacksOfDay[0][0].dateTime * 1000) }}
        </div>

        <div
          class="conversation-messages__per-day-packs"
          v-for="(dailyMessagePack, index2) in dailyMessagePacksOfDay"
          :key="index2"
        >
          <div v-if="dailyMessagePack[0].type === 'MESSAGE'">
            <div
              class="conversation-messages__per-day-packs-author"
              :class="{
                'conversation-messages__per-day-packs-author--with-new-message':
                  dailyMessagePack[dailyMessagePack.length - 1].watchVisible,
              }"
            >
              <strong>
                <ChatterLabel :chatter-id="dailyMessagePack[0].from" />
              </strong>
              @
              {{ $filters.timeToMinutes(dailyMessagePack[0].dateTime * 1000) }}
            </div>
            <div class="conversation-messages__per-day-packs-pack">
              <ConversationMessage
                v-for="dailyMessage in dailyMessagePack"
                :message-data="dailyMessage"
                :key="dailyMessage.id"
                :ref="
                  (el) => {
                    if (el) divs[dailyMessage.index] = el;
                  }
                "
                v-observe-visibility="
                  dailyMessage.watchVisible
                    ? markViewedMessage(dailyMessage)
                    : false
                "
              />
            </div>
          </div>
          <div v-if="dailyMessagePack[0].type === 'CHATTER_JOINED'">
            <ConversationChatterJoined
              :chatter-data="dailyMessagePack[0]"
              :ref="
                (el) => {
                  if (el) divs[dailyMessagePack[0].index] = el;
                }
              "
              v-observe-visibility="
                dailyMessagePack[0].watchVisible
                  ? markViewedMessage(dailyMessagePack[0])
                  : false
              "
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import type { PropType } from "vue";
import {
  computed,
  defineComponent,
  nextTick,
  onBeforeUpdate,
  onMounted,
  ref,
  toRefs,
  watch,
} from "vue";
import type { MarkableConversationData } from "@/composables/use-conversation";
import ConversationMessage from "@/components/chat/conversation/ConversationMessage.vue";
import { DateTime } from "luxon";
import type { ChatterId, ReadMarkers } from "@/domain/model";
import ChatterLabel from "@/components/chat/ChatterLabel.vue";
import ConversationChatterJoined from "@/components/chat/conversation/ConversationChatterJoined.vue";
import ConversationReadMarkers from "@/components/chat/conversation/ConversationReadMarkers.vue";
import useConversationReadMarkers from "@/composables/use-conversation-read-markers";
import { useUserStore } from "@/stores/user";
import { useUiStore } from "@/stores/ui";

const groupBy = <K, V>(
  list: Array<V>,
  keyGetter: (input: V) => K
): Map<K, Array<V>> => {
  const map = new Map();
  list.forEach((item) => {
    const key = keyGetter(item);
    const collection = map.get(key);
    if (!collection) {
      map.set(key, [item]);
    } else {
      collection.push(item);
    }
  });
  return map;
};

const packSameUserSameBlockOfMessage = (
  dailyMessages: Array<MarkableConversationData>
): Array<Array<MarkableConversationData>> => {
  const packs: MarkableConversationData[][] = [];
  let pack: MarkableConversationData[] = [];

  const firstDailyMessage = dailyMessages[0];
  let currentChatter: ChatterId = firstDailyMessage.from;
  let previousMessageDateTime: DateTime = DateTime.fromSeconds(
    firstDailyMessage.dateTime
  );
  let previousType = "";
  dailyMessages.forEach((dailyMessage) => {
    if (dailyMessage.type === "CREATION") {
      // use next pack
      if (pack.length > 0) packs.push(pack);
      pack = [dailyMessage];
    } else if (dailyMessage.type === "CHATTER_JOINED") {
      // use next pack
      if (pack.length > 0) packs.push(pack);
      pack = [dailyMessage];
    } else if (dailyMessage.type === "MESSAGE") {
      if (previousType !== "MESSAGE" || dailyMessage.from === currentChatter) {
        const diffInMinutes = DateTime.fromSeconds(dailyMessage.dateTime).diff(
          previousMessageDateTime,
          "minutes"
        );
        if (diffInMinutes.minutes < 3) {
          // complete pack
          pack.push(dailyMessage);
        } else {
          // use next pack
          if (pack.length > 0) packs.push(pack);
          pack = [dailyMessage];
        }
      } else {
        // use next pack
        if (pack.length > 0) packs.push(pack);
        pack = [dailyMessage];
      }

      previousMessageDateTime = DateTime.fromSeconds(dailyMessage.dateTime);
      currentChatter = dailyMessage.from;
      previousType = dailyMessage.type;
    }
  });
  packs.push(pack);

  return packs;
};

export default defineComponent({
  name: "ConversationMessages",
  components: {
    ConversationReadMarkers,
    ConversationChatterJoined,
    ChatterLabel,
    ConversationMessage,
  },
  props: {
    content: {
      type: Object as PropType<MarkableConversationData[]>,
      required: true,
    },
    readMarkers: {
      type: Object as PropType<ReadMarkers>,
      required: true,
    },
  },
  setup(props) {
    const messagesContainer = ref<HTMLElement | null>(null);

    const userStore = useUserStore();
    const uiStore = useUiStore();
    const { content, readMarkers } = toRefs(props);

    const dailyMessagePacksPerDay = ref<
      Array<Array<Array<MarkableConversationData>>>
    >([]);
    const computeDailyMessagePacksPerDay = () => {
      const packsPerDay: Array<Array<Array<MarkableConversationData>>> = [];
      const messagesByDay: Map<
        string,
        Array<MarkableConversationData>
      > = groupBy(content.value, (m) =>
        DateTime.fromSeconds(m.dateTime).toISODate()
      );
      messagesByDay.forEach((dailyMessages) => {
        packsPerDay.push(packSameUserSameBlockOfMessage(dailyMessages));
      });
      dailyMessagePacksPerDay.value = packsPerDay;
    };

    const shouldStickToBottom = ref<boolean>(true);
    const scrollToBottom = () => {
      nextTick(() => {
        // shouldStickToBottom.value = true
        if (messagesContainer.value != null) {
          messagesContainer.value.scrollTop =
            messagesContainer.value?.scrollHeight;
        }
      });
    };
    onMounted(scrollToBottom);

    const { divs, clearDivs, positionedReadMarkers, updateReadMarkers } =
      useConversationReadMarkers(readMarkers, content);
    onBeforeUpdate(clearDivs);
    onMounted(updateReadMarkers);

    watch(content, (newMessages) => {
      if (
        shouldStickToBottom.value ||
        (newMessages.length > 0 &&
          newMessages[newMessages.length - 1].from === userStore.chatterId)
      ) {
        scrollToBottom();
      }
      computeDailyMessagePacksPerDay();
      updateReadMarkers();
    });
    watch(
      () => uiStore.visible,
      (screenVisible) => {
        // note: one could delay and debounce
        if (
          screenVisible &&
          shouldStickToBottom.value &&
          content.value.length > 0
        ) {
          const lastMessage = content.value[content.value.length - 1];
          if (lastMessage.watchVisible) {
            lastMessage.onVisible();
          }
        }
      }
    );

    const markViewedMessage = (dailyMessage: MarkableConversationData) => {
      return dailyMessage.watchVisible
        ? {
            callback: (isVisible: boolean) => {
              if (isVisible) {
                dailyMessage.onVisible();
              }
            },
            // throttle: 1500,
            // once: true,
          }
        : false;
    };

    const hasNewMessage = computed(() => {
      const dailyMessagePacks =
        dailyMessagePacksPerDay.value[
          dailyMessagePacksPerDay.value.length - 1
        ] || [];
      const dailyMessagePack =
        dailyMessagePacks[dailyMessagePacks.length - 1] || [];
      return (
        dailyMessagePack.length > 0 &&
        dailyMessagePack[dailyMessagePack.length - 1].watchVisible
      );
    });

    const handleScroll = (e: any) => {
      // console.log('scrollTop', e.target.scrollTop, 'scrollHeight', e.target.scrollHeight, 'offsetHeight', e.target.offsetHeight)
      shouldStickToBottom.value =
        e.target.scrollTop === e.target.scrollHeight - e.target.offsetHeight;
    };

    return {
      scrollToBottom,
      markViewedMessage,
      messagesContainer,
      dailyMessagePacksPerDay,
      hasNewMessage,
      handleScroll,
      shouldStickToBottom,
      divs,
      positionedReadMarkers,
    };
  },
});
</script>

<style scoped lang="scss">
.conversation-messages {
  position: relative;
  min-height: inherit;
  max-height: inherit;

  &__wrapper {
    position: relative;
    overflow: -moz-scrollbars-vertical;
    overflow-y: scroll;
    width: 100%;
    min-height: inherit;
    max-height: inherit;
  }

  .separator {
    display: flex;
    align-items: center;
    text-align: center;

    &::before,
    &::after {
      content: "";
      flex: 1;
      border-bottom: 1px solid #000;
    }

    &:not(:empty)::before {
      margin-right: 0.25em;
    }

    &:not(:empty)::after {
      margin-left: 0.25em;
    }
  }

  &__per-day {
    padding-left: 10px;
    padding-right: 10px;
    padding-top: 5px;

    &:not(:last-child) {
      padding-bottom: 35px;
    }

    &-packs {
      text-align: left;

      &:not(:last-child) {
        padding-bottom: 20px;
      }

      &-author {
        &--with-new-message {
          font-weight: bold;
        }
      }

      &-pack:not(:last-child) {
        padding-bottom: 12px;
      }
    }
  }

  &__arrow {
    position: absolute;
    z-index: 2;
    right: 20px;
    bottom: 20px;

    &--with-new-message {
      box-shadow: 0 0 30px 3px #0080ff;
    }
  }
}
</style>
