import {MutationTree} from 'vuex'
import {Chatter, ChatterId, Conversation, ConversationData, ConversationDataId, ConversationId, State} from './state'

export enum MutationType {
    InstallChatter = 'INSTALL_CHATTER',
    InstallConversation = 'INSTALL_CONVERSATION',
    UpdateConversationAddMessage = 'UPDATE_CONVERSATION_ADD_MESSAGE',
    UpdateConversationMarkerAsRead = 'UPDATE_CONVERSATION_MARKER_AS_READ',
}

export type Mutations = {
    [MutationType.InstallChatter](state: State, chatter: Chatter): void
    [MutationType.InstallConversation](state: State, conversation: Conversation): void
    [MutationType.UpdateConversationAddMessage](
        state: State,
        value: { conversationId: ConversationId, conversationData: ConversationData }
    ): void
    [MutationType.UpdateConversationMarkerAsRead](
        state: State,
        value: { conversationId: ConversationId, chatterId: ChatterId, conversationDataId: ConversationDataId }
    ): void
    // [MutationType.EditTask](
    //     state: State,
    //     task: Partial<TaskItem> & { id: number }
    // ): void
    // [MutationType.UpdateTask](
    //     state: State,
    //     task: Partial<TaskItem> & { id: number }
    // ): void
    // [MutationType.SetLoading](state: State, value: boolean): void
    // [MutationType.SetCreateModal](state: State, value: boolean): void
    // [MutationType.SetEditModal](state: State, value: {showModal: boolean, taskId: number|undefined}): void
    // [MutationType.SetTaskModal](state: State, value: {showModal: boolean, taskId: number|undefined}): void
}

export const mutations: MutationTree<State> & Mutations = {
    [MutationType.InstallChatter](state, chatter) {
        state.chatters[chatter.id] = chatter
    },
    [MutationType.InstallConversation](state, conversation) {
        state.conversations[conversation.id] = conversation
    },
    [MutationType.UpdateConversationAddMessage](state, {conversationId, conversationData}) {
        state.conversations[conversationId].events.push(conversationData)
    },
    [MutationType.UpdateConversationMarkerAsRead](state, {conversationId, chatterId, conversationDataId}) {
        state.conversations[conversationId].markedAsRead[chatterId] = conversationDataId
    },
    // [MutationType.EditTask](state, Task) {
    //     const task = state.tasks.findIndex(element => element.id === Task.id)
    //     if (task === -1) return
    //     //If Task exist in the state, toggle the editing property
    //     state.tasks[task] = { ...state.tasks[task], editing: !state.tasks[task].editing }
    //     console.log("taskino", state.tasks[task])
    // },
    // [MutationType.UpdateTask](state, Task) {
    //     state.tasks = state.tasks.map(task => {
    //         if(task.id === Task.id) {
    //             return {...task, ...Task}
    //         }
    //         return task;
    //     })
    // },
    // [MutationType.SetLoading](state, value) {
    //     state.loading = value
    //     console.log("I am loading...")
    // },
    // [MutationType.SetCreateModal](state, value) {
    //     state.showCreateModal = value
    // },
    // [MutationType.SetEditModal](state, value) {
    //     state.showEditModal = value.showModal
    //     state.editModalTaskId = value.taskId
    // },
    // [MutationType.SetTaskModal](state, {showModal, taskId}) {
    //     state.showTaskModal = showModal
    //     state.showTaskId = taskId
    // }
}