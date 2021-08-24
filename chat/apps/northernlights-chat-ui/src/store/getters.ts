import {GetterTree} from 'vuex'
import {Chatter, ChatterId, Conversation, ConversationId, State} from './state'

export type Getters = {
    // completedTaskCount(state: State): number
    // totalTaskCount(state: State): number
    getChatterById(state: State): (id: ChatterId) => Chatter;
    getConversationById(state: State): (id: ConversationId) => Conversation;
    getConversationWithChatterId(state: State): (id: ChatterId) => Conversation | undefined;
    getConversationIdWithChatterId(state: State): (id: ChatterId) => ConversationId | undefined;
}

export const getters: GetterTree<State, State> & Getters = {
    getChatterById: (state) => (id: ChatterId): Chatter => {
        const chatter = state.chatters.get(id);
        if (chatter == undefined) {
            throw new Error("Using unknown chatter id : " + id);
        }
        return chatter
    },
    // completedTaskCount(state) {
    //     return state.tasks.filter(element => element.completed).length
    // },
    // totalTaskCount(state) {
    //     return state.tasks.length
    // },
    getConversationById: (state) => (id: ConversationId): Conversation => {
        const conversation = state.conversations.get(id);
        if (conversation == undefined) {
            throw new Error("Using unknown conversation id : " + id);
        }
        return conversation
    },
    getConversationWithChatterId: (state) => (chatterId: ChatterId): Conversation | undefined => {
        return Array.from(state.conversations.values())
            .find(c => c.dialogue === true
                && state.chatterId !== undefined && c.participants.includes(state.chatterId)
                && c.participants.includes(chatterId))
    },
    getConversationIdWithChatterId: (state) => (chatterId: ChatterId): ConversationId | undefined => {
        return Array.from(state.conversations.values())
            .find(c => c.dialogue === true
                && state.chatterId !== undefined && c.participants.includes(state.chatterId)
                && c.participants.includes(chatterId))?.id
    }
}