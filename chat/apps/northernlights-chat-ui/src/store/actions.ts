import {ActionContext, ActionTree} from 'vuex'
import {Mutations, MutationType} from './mutations'
import {Chatter, ChatterId, Conversation, ConversationData, ConversationDataId, ConversationId, State} from './state'

export enum ActionTypes {
    InstallChatter = 'INSTALL_CHATTER',
    InstallConversation = 'INSTALL_CONVERSATION',
    UpdateConversationMessage = 'UPDATE_CONVERSATION_ADD_EVENT',
    UpdateConversationMarkAsRead = 'UPDATE_CONVERSATION_MARKER_AS_READ'
}

type ActionAugments = Omit<ActionContext<State, State>, 'commit'> & {
    commit<K extends keyof Mutations>(
        key: K,
        payload: Parameters<Mutations[K]>[1]
    ): ReturnType<Mutations[K]>
}
export type Actions = {
    [ActionTypes.InstallChatter](context: ActionAugments, chatter: Chatter): void
    [ActionTypes.InstallConversation](context: ActionAugments, conversation: Conversation): void
    [ActionTypes.UpdateConversationMessage](context: ActionAugments, value: { conversationId: ConversationId, conversationData: ConversationData }): void
    [ActionTypes.UpdateConversationMarkAsRead](context: ActionAugments, value: { conversationId: ConversationId, chatterId: ChatterId, conversationDataId: ConversationDataId }): void
}

// const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))
export const actions: ActionTree<State, State> & Actions = {
    async [ActionTypes.InstallChatter]({commit}, chatter) {
        commit(MutationType.InstallChatter, chatter)
        // await sleep(1000)
        // commit(MutationType.SetLoading, false)
        // commit(MutationType.SetTasks, [
        //     {
        //         id: 1,
        //         title: 'Create a new programming language',
        //         description: "The programing language should have full typescript support ",
        //         createdBy: "Emmanuel John",
        //         assignedTo: "Saviour Peter",
        //         completed: false,
        //         editing: false
        //     }
        // ])
    },
    async [ActionTypes.InstallConversation]({commit}, conversation) {
        commit(MutationType.InstallConversation, conversation)
        // await sleep(1000)
        // commit(MutationType.SetLoading, false)
        // commit(MutationType.SetTasks, [
        //     {
        //         id: 1,
        //         title: 'Create a new programming language',
        //         description: "The programing language should have full typescript support ",
        //         createdBy: "Emmanuel John",
        //         assignedTo: "Saviour Peter",
        //         completed: false,
        //         editing: false
        //     }
        // ])
    },
    async [ActionTypes.UpdateConversationMessage]({commit}, {conversationId, conversationData}) {
        commit(MutationType.UpdateConversationAddMessage, {
            conversationId,
            conversationData
        })
    },
    async [ActionTypes.UpdateConversationMarkAsRead]({commit}, {conversationId, chatterId, conversationDataId}) {
        commit(MutationType.UpdateConversationMarkerAsRead, {conversationId, chatterId, conversationDataId})
    }
}