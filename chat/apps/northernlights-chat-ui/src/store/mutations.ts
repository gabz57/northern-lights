/* eslint-disable no-debugger */

import {MutationTree} from 'vuex'
import {Chatter, ChatterId, Conversation, ConversationData, ConversationId, MarkedAsRead, State} from './state'

export enum MutationType {
    UpdateSseOpenStatus = 'UPDATE_SSE_OPEN_STATUS',
    UpdateEventSource = 'UPDATE_EVENT_SOURCE',
    SetSseWanted = 'SET_SSE_WANTED',
    SetSseAutoConnect = 'SET_SSE_AUTO_CONNECT',
    ClearChatterState = 'CLEAR_CHATTER_STATE',
    SetChatterId = 'SET_CHATTER_ID',
    InstallChatter = 'INSTALL_CHATTER',
    InstallConversation = 'INSTALL_CONVERSATION',
    UpdateConversationAddMessage = 'UPDATE_CONVERSATION_ADD_MESSAGE',
    UpdateConversationMarkerAsRead = 'UPDATE_CONVERSATION_MARKER_AS_READ',
}

export type Mutations = {
    [MutationType.UpdateSseOpenStatus](state: State, isOpen: boolean): void
    [MutationType.UpdateEventSource](state: State, eventSource: EventSource): void
    [MutationType.SetSseWanted](state: State, enabled: boolean): void
    [MutationType.SetSseAutoConnect](state: State, enabled: boolean): void
    [MutationType.ClearChatterState](state: State): void
    [MutationType.SetChatterId](state: State, chatterId: ChatterId): void
    [MutationType.InstallChatter](state: State, chatter: Chatter): void
    [MutationType.InstallConversation](state: State, conversation: Conversation): void
    [MutationType.UpdateConversationAddMessage](
        state: State,
        value: { conversationId: ConversationId, conversationData: ConversationData[] }
    ): void
    [MutationType.UpdateConversationMarkerAsRead](
        state: State,
        value: { conversationId: ConversationId, readMarkers: MarkedAsRead }
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
    [MutationType.UpdateSseOpenStatus](state: State, isOpen: boolean) {
        state.sse.sseOpen = isOpen
    },
    [MutationType.UpdateEventSource](state: State, eventSource: EventSource) {
        state.sse.eventSource = eventSource
    },
    [MutationType.SetSseWanted](state: State, wanted: boolean) {
        state.sse.sseWanted = wanted
    },
    [MutationType.SetSseAutoConnect](state: State, enabled: boolean) {
        state.sse.sseAutoConnect = enabled
    },
    [MutationType.ClearChatterState](state: State) {
        state.chatters.clear();
        state.conversations.clear()
    },
    [MutationType.SetChatterId](state, chatterId) {
        state.chatterId = chatterId
    },
    [MutationType.InstallChatter](state, chatter) {
        state.chatters.set(chatter.id, chatter)
    },
    [MutationType.InstallConversation](state, conversation) {
        state.conversations.set(conversation.id, conversation)
    },
    [MutationType.UpdateConversationAddMessage](state, {conversationId, conversationData}) {
        conversationData.forEach(cData => state.conversations.get(conversationId)?.data.push(cData))
    },
    [MutationType.UpdateConversationMarkerAsRead](state, {conversationId, readMarkers}) {
        readMarkers.forEach((conversationDataId, chatterId) => {
            state.conversations.get(conversationId)?.markedAsRead.set(chatterId, conversationDataId)
        })
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