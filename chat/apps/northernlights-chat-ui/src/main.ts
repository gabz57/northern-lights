import { createApp } from "vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import "./registerServiceWorker";
import router from "./router";
import VueObserveVisibility from "vue-observe-visibility";
import {DateTime} from "luxon";

const app = createApp(App);
app.use(VueObserveVisibility);
app.use(createPinia());
app.use(router);

app.config.globalProperties.$filters = {
  dateTime(value: number | undefined) {
    if (value === undefined) return undefined;
    return DateTime.fromMillis(value).toLocaleString(DateTime.DATETIME_SHORT);
  },
  date(value: number | undefined) {
    if (value === undefined) return undefined;
    return DateTime.fromMillis(value).toLocaleString(DateTime.DATE_SHORT);
  },
  time(value: number | undefined) {
    if (value === undefined) return undefined;
    return DateTime.fromMillis(value).toLocaleString(DateTime.TIME_24_WITH_SECONDS);
  },
  timeToMinutes(value: number | undefined) {
    if (value === undefined) return undefined;
    return DateTime.fromMillis(value).toLocaleString(DateTime.TIME_24_SIMPLE);
  },
};
app.mount("#app");
