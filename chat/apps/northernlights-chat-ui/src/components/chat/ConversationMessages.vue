<template>
  <div class="conversation-messages">
    <div class="conversation-messages--per-day" v-for="(dailyMessagePacksOfDay, index) in dailyMessagePacksPerDay"
         :key="index">

      <div class="separator">{{ $filters.date(dailyMessagePacksOfDay[0][0].dateTime * 1000) }}</div>

      <div class="conversation-messages--per-day-packs" v-for="(dailyMessagePack, index2) in dailyMessagePacksOfDay"
           :key="index2">
        <div class="conversation-messages--per-day-packs-author"
             :class="{'conversation-messages--per-day-packs-author--with-new-message': dailyMessagePack[dailyMessagePack.length - 1].watchVisible}">
          From: {{ dailyMessagePack[0].author }}
        </div>
        <div class="conversation-messages--per-day-packs-pack" v-for="(dailyMessage, index3) in dailyMessagePack"
             :key="index3">
          <ConversationMessage :message="dailyMessage"
                               v-observe-visibility="index3 === dailyMessagePack.length - 1
                               && dailyMessage.watchVisible ? {
                                    callback: (isVisible, elem) => {
                                     if (isVisible) {
                                       dailyMessage.onVisible()
                                     }
                                    },
                                    throttle: 1500,
                                    once: true,
                                  } : false"/>
        </div>
        <!--        <br/>-->
      </div>
      <!--      <br/>-->
    </div>
  </div>
</template>

<script lang="ts">
/* eslint-disable no-debugger */
import {computed, defineComponent, PropType, toRefs} from "vue";
import {ConversationDataWithMarkers} from "@/composables/use-conversation";
import ConversationMessage from "@/components/chat/ConversationMessage.vue";
import moment from "moment/moment";
import {DateTime} from "luxon";
import {ChatterId} from "@/store/state";

const groupBy = <K, V>(list: Array<V>, keyGetter: (input: V) => K): Map<K, Array<V>> => {
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

const packSameUserSameBlockOfMessage = (dailyMessages: Array<ConversationDataWithMarkers>): Array<Array<ConversationDataWithMarkers>> => {

  const packs: ConversationDataWithMarkers[][] = [];
  let pack: ConversationDataWithMarkers[] = [];

  const firstDailyMessage = dailyMessages[0];

  let currentChatter: ChatterId = firstDailyMessage.author;
  let previousMessageDateTime: DateTime = DateTime.fromSeconds(firstDailyMessage.dateTime);
  dailyMessages.forEach(dailyMessage => {

    if (dailyMessage.author === currentChatter) {

      const diffInMinutes = DateTime.fromSeconds(dailyMessage.dateTime).diff(previousMessageDateTime, 'minutes');

      if (diffInMinutes.minutes < 3) {
        pack.push(dailyMessage)
      } else {
        // next pack
        packs.push(pack)
        pack = [dailyMessage]
      }
    } else {
      // next pack
      packs.push(pack)
      pack = [dailyMessage]
    }
    previousMessageDateTime = DateTime.fromSeconds(dailyMessage.dateTime)
    currentChatter = dailyMessage.author
  })
  packs.push(pack)

  return packs;
};

export default defineComponent({
  name: "ConversationMessages",
  components: {ConversationMessage},
  props: {
    messages: {
      type: Object as PropType<ConversationDataWithMarkers[]>,
      required: true
    }
  },
  setup(props) {
    const {messages} = toRefs(props)
    const dailyMessagePacksPerDa = computed(() => {
      const dailyMessagePacksPerDay: Array<Array<Array<ConversationDataWithMarkers>>> = []
      const messagesByDay: Map<string, Array<ConversationDataWithMarkers>> = groupBy(messages.value, m => "" + moment(m.dateTime * 1000).year() + moment(m.dateTime * 1000).dayOfYear());
      messagesByDay.forEach((dailyMessages, k) => {
        dailyMessagePacksPerDay.push(packSameUserSameBlockOfMessage(dailyMessages))
      })
      return dailyMessagePacksPerDay
    })

    return {
      dailyMessagePacksPerDay: dailyMessagePacksPerDa
    }
  }
});

</script>

<style scoped lang="scss">
.conversation-messages {
  overflow: -moz-scrollbars-vertical;
  overflow-y: scroll;
  height: 400px;
  border: 1px solid black;

  .separator {
    display: flex;
    align-items: center;
    text-align: center;
    &::before, &::after {
      content: '';
      flex: 1;
      border-bottom: 1px solid #000;
    }

    &:not(:empty)::before {
      margin-right: .25em;
    }

    &:not(:empty)::after {
      margin-left: .25em;
    }
  }

  &--per-day{
    padding-top: 5px;

    &:not(:last-child) {
      padding-bottom: 35px;
    }

    &-packs {
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
}
</style>