/* eslint-disable no-debugger */

import {MutationTree} from 'vuex'
import {
    Chatter,
    ChatterId,
    Conversation,
    ConversationData,
    ConversationId,
    ConversationPart,
    ReadMarkers,
    State
} from './state'

export enum MutationType {
    UpdateSseOpenStatus = 'UPDATE_SSE_OPEN_STATUS',
    UpdateEventSource = 'UPDATE_EVENT_SOURCE',
    SetSseWanted = 'SET_SSE_WANTED',
    SetSseAutoConnect = 'SET_SSE_AUTO_CONNECT',
    ClearChatterState = 'CLEAR_CHATTER_STATE',
    SetChatterId = 'SET_CHATTER_ID',
    InstallChatter = 'INSTALL_CHATTER',
    InstallConversation = 'INSTALL_CONVERSATION',
    InstallConversationPart = 'INSTALL_CONVERSATION_PART',
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
    [MutationType.InstallConversationPart](state: State, conversation: ConversationPart): void
    [MutationType.UpdateConversationAddMessage](
        state: State,
        value: { conversationId: ConversationId, conversationData: ConversationData[] }
    ): void
    [MutationType.UpdateConversationMarkerAsRead](
        state: State,
        value: { conversationId: ConversationId, readMarkers: ReadMarkers }
    ): void
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
    [MutationType.InstallConversationPart](state, conversationPart) {
        const conversation = state.conversations.get(conversationPart.id);
        if (conversation === undefined) return
        conversationPart.data.forEach((conversationData: ConversationData) => conversation.data.push(conversationData))
        conversationPart.readMarkers.forEach((conversationId: ConversationId, chatterId: ChatterId) =>
            conversation.readMarkers.set(chatterId, conversationId))
    },
    [MutationType.UpdateConversationAddMessage](state, {conversationId, conversationData}) {
        conversationData.forEach(cData => state.conversations.get(conversationId)?.data.push(cData))
    },
    [MutationType.UpdateConversationMarkerAsRead](state, {conversationId, readMarkers}) {
        readMarkers.forEach((conversationDataId, chatterId) =>
            state.conversations.get(conversationId)?.readMarkers.set(chatterId, conversationDataId))
    },
}