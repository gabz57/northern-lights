import {GetterTree} from 'vuex'
import {Chatter, ChatterId, State} from './state'

export type Getters = {
    // completedTaskCount(state: State): number
    // totalTaskCount(state: State): number
    getChatterById(state: State): (id: ChatterId) => Chatter
}

export const getters: GetterTree<State, State> & Getters = {
    // completedTaskCount(state) {
    //     return state.tasks.filter(element => element.completed).length
    // },
    // totalTaskCount(state) {
    //     return state.tasks.length
    // },
    getChatterById: (state) => (id: ChatterId) => {
        return state.chatters[id]
    }
}