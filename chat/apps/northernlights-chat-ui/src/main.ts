import {createApp} from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia';
import './registerServiceWorker'
import router from './router'
import VueObserveVisibility from 'vue-observe-visibility'
import moment from "moment/moment";

const app = createApp(App)
app.use(VueObserveVisibility)
app.use(createPinia());
app.use(router)

app.config.globalProperties.$filters = {
    dateTime(value: number | undefined) {
        if (value === undefined) return undefined
        return moment(value).format('DD/MM/YYYY, HH:mm:ss')
    },
    date(value: number | undefined) {
        if (value === undefined) return undefined
        return moment(value).format('DD/MM/YYYY')
    },
    time(value: number | undefined) {
        if (value === undefined) return undefined
        return moment(value).format('HH:mm:ss')
    },
    timeToMinutes(value: number | undefined) {
        if (value === undefined) return undefined
        return moment(value).format('HH:mm')
    }
}
app.mount('#app');
