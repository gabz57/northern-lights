import {MutationTree} from 'vuex'
import {
    Chatter,
    ChatterId,
    Conversation, ConversationChatterData,
    ConversationData,
    ConversationId, ConversationMessageData,
    ConversationPart, Profile,
    ReadMarkers,
    State
} from './state'

export enum MutationType {
    UpdateNavigatorOnlineStatus = 'UPDATE_NAVIGATOR_ONLINE_STATUS',
    UpdateChatVisibility = 'UPDATE_CHAT_VISIBILITY',
    UpdateSseOpenStatus = 'UPDATE_SSE_OPEN_STATUS',
    UpdateEventSource = 'UPDATE_EVENT_SOURCE',
    SetSseWanted = 'SET_SSE_WANTED',
    SetSseAutoConnect = 'SET_SSE_AUTO_CONNECT',
    ClearChatterState = 'CLEAR_CHATTER_STATE',
    SetJwt = 'SET_JWT',
    SetChatterId = 'SET_CHATTER_ID',
    SetSelectedConversationId = 'SET_SELECTED_CONVERSATION_ID',
    SetEditingProfile = 'SET_EDITING_PROFILE',
    SetCreatingConversation = 'SET_CREATING_CONVERSATION',
    InstallProfile = 'INSTALL_PROFILE',
    InstallChatter = 'INSTALL_CHATTER',
    InstallConversation = 'INSTALL_CONVERSATION',
    InstallConversationPart = 'INSTALL_CONVERSATION_PART',
    UpdateConversationAddMessage = 'UPDATE_CONVERSATION_ADD_MESSAGE',
    UpdateConversationAddChatter = 'UPDATE_CONVERSATION_ADD_CHATTER',
    UpdateConversationMarkerAsRead = 'UPDATE_CONVERSATION_MARKER_AS_READ',
}

export type Mutations = {
    [MutationType.UpdateNavigatorOnlineStatus](state: State, isOnline: boolean): void
    [MutationType.UpdateChatVisibility](state: State, isVisible: boolean): void
    [MutationType.UpdateSseOpenStatus](state: State, isOpen: boolean): void
    [MutationType.UpdateEventSource](state: State, eventSource: EventSource): void
    [MutationType.SetSseWanted](state: State, enabled: boolean): void
    [MutationType.SetSseAutoConnect](state: State, enabled: boolean): void
    [MutationType.ClearChatterState](state: State): void
    [MutationType.SetJwt](state: State, jwt: string): void
    [MutationType.SetChatterId](state: State, chatterId: ChatterId): void
    [MutationType.SetSelectedConversationId](state: State, conversationID: ConversationId | undefined): void
    [MutationType.SetEditingProfile](state: State, enabled: boolean): void
    [MutationType.SetCreatingConversation](state: State, enabled: boolean): void
    [MutationType.InstallProfile](state: State, profile: Profile): void
    [MutationType.InstallChatter](state: State, chatter: Chatter): void
    [MutationType.InstallConversation](state: State, conversation: Conversation): void
    [MutationType.InstallConversationPart](state: State, conversation: ConversationPart): void
    [MutationType.UpdateConversationAddMessage](
        state: State,
        value: { conversationId: ConversationId, conversationMessageData: ConversationMessageData }
    ): void
    [MutationType.UpdateConversationAddChatter](
        state: State,
        value: { conversationId: ConversationId, conversationChatterData: ConversationChatterData }
    ): void
    [MutationType.UpdateConversationMarkerAsRead](
        state: State,
        value: { conversationId: ConversationId, readMarkers: ReadMarkers }
    ): void
}

export const mutations: MutationTree<State> & Mutations = {
    [MutationType.UpdateNavigatorOnlineStatus](state: State, isOnline: boolean) {
        console.log("MutationType.UpdateNavigatorOnlineStatus", isOnline)
        state.ui.online = isOnline
    },
    [MutationType.UpdateChatVisibility](state: State, isVisible: boolean) {
        console.log("MutationType.UpdateChatVisibility", isVisible)
        state.ui.visible = isVisible
    },
    [MutationType.UpdateSseOpenStatus](state: State, isOpen: boolean) {
        console.log("MutationType.UpdateSseOpenStatus", isOpen)
        state.sse.sseOpen = isOpen
    },
    [MutationType.UpdateEventSource](state: State, eventSource: EventSource) {
        state.sse.eventSource = eventSource
    },
    [MutationType.SetSseWanted](state: State, wanted: boolean) {
        console.log("MutationType.SetSseWanted", wanted)
        state.sse.sseWanted = wanted
    },
    [MutationType.SetSseAutoConnect](state: State, enabled: boolean) {
        console.log("MutationType.SetSseAutoConnect", enabled)
        state.sse.sseAutoConnect = enabled
    },
    [MutationType.ClearChatterState](state: State) {
        // state.ui.selectedConversationId = undefined;
        state.chatters.clear();
        state.conversations.clear()
    },
    [MutationType.SetChatterId](state, chatterId) {
        state.chatterId = chatterId
    },
    [MutationType.SetJwt](state, jwt) {
        state.jwt = jwt
    },
    [MutationType.SetSelectedConversationId](state, conversationId) {
        state.ui.selectedConversationId = conversationId
    },
    [MutationType.SetEditingProfile](state, enabled) {
        state.ui.editingProfile = enabled
    },
    [MutationType.SetCreatingConversation](state, enabled) {
        state.ui.creatingConversation = enabled
    },
    [MutationType.InstallProfile](state, profile) {
        state.profile = profile
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
    [MutationType.UpdateConversationAddMessage](state, {conversationId, conversationMessageData}) {
        state.conversations.get(conversationId)?.data.push(conversationMessageData)
    },
    [MutationType.UpdateConversationAddChatter](state, {conversationId, conversationChatterData}) {
        state.conversations.get(conversationId)?.participants.push(conversationChatterData.chatterId)
        state.conversations.get(conversationId)?.data.push(conversationChatterData)
    },
    [MutationType.UpdateConversationMarkerAsRead](state, {conversationId, readMarkers}) {
        readMarkers.forEach((conversationDataId, chatterId) =>
            state.conversations.get(conversationId)?.readMarkers.set(chatterId, conversationDataId))
    },
}
