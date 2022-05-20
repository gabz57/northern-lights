import {ActionContext, ActionTree} from 'vuex'
import {Mutations, MutationType} from './mutations'
import {
    Chatter,
    ChatterId,
    Conversation,
    ConversationChatterData,
    ConversationDataId,
    ConversationId,
    ConversationMessageData,
    ConversationPart, Profile,
    ReadMarkers,
    State
} from './state'
import {chatApiClient} from "@/services/ChatApiClient";
import {userApiClient} from "@/services/UserApiClient";

export enum ActionTypes {
    // UI status
    UpdateNavigatorOnlineStatus = "UPDATE_NAVIGATOR_ONLINE_STATUS",
    UpdateChatVisibility = "UPDATE_CHAT_VISIBILITY",
    SetSelectedConversationId = 'SET_SELECTED_CONVERSATION_ID',
    DeselectConversationId = 'DESELECT_CONVERSATION_ID',
    SetEditingProfile = 'SET_EDITING_PROFILE',
    SetCreatingConversation = 'SET_CREATING_CONVERSATION',
    // SSE Mngt
    StoreSseOpenStatus = "STORE_SSE_OPEN_STATUS",
    StoreEventSource = "STORE_EVENT_SOURCE",
    EnableSseWanted = "ENABLE_SSE",
    DisableSseWanted = "DISABLE_SSE",
    EnableSseAutoConnect = "ENABLE_SSE_AUTO_CONNECT",
    DisableSseAutoConnect = "DISABLE_SSE_AUTO_CONNECT",
    ClearChatterState = "CLEAR_CHATTER_STATE",
    // SSE
    InstallProfile = 'INSTALL_PROFILE',
    InstallChatter = 'INSTALL_CHATTER',
    InstallConversation = 'INSTALL_CONVERSATION',
    InstallConversationPart = 'INSTALL_CONVERSATION_PART',
    UpdateConversationAddMessage = 'UPDATE_CONVERSATION_ADD_MESSAGE',
    UpdateConversationAddChatter = 'UPDATE_CONVERSATION_ADD_CHATTER',
    UpdateConversationMarkAsRead = 'UPDATE_CONVERSATION_MARKER_AS_READ',
    // USER
    SetJwt = 'SET_JWT',
    SetChatterId = 'SET_CHATTER_ID',
    SendMessage = 'SEND_MESSAGE',
    MarkAsRead = 'MARK_AS_READ',
    CreateConversation = 'CREATE_CONVERSATION',
    InviteChatter = 'INVITE_CHATTER',
}

type ActionAugments = Omit<ActionContext<State, State>, 'commit'> & {
    commit<K extends keyof Mutations>(
        key: K,
        payload: Parameters<Mutations[K]>[1]
    ): ReturnType<Mutations[K]>
}
export type Actions = {
    [ActionTypes.UpdateNavigatorOnlineStatus](context: ActionAugments, isOnline: boolean): void
    [ActionTypes.UpdateChatVisibility](context: ActionAugments, isVisible: boolean): void
    [ActionTypes.SetSelectedConversationId](context: ActionAugments, conversationId: ConversationId): void
    [ActionTypes.DeselectConversationId](context: ActionAugments): void
    [ActionTypes.SetEditingProfile](context: ActionAugments, enabled: boolean): void
    [ActionTypes.SetCreatingConversation](context: ActionAugments, enabled: boolean): void
    // SSE Mngt
    [ActionTypes.StoreSseOpenStatus](context: ActionAugments, isOpen: boolean): void
    [ActionTypes.StoreEventSource](context: ActionAugments, eventSource: EventSource): void
    [ActionTypes.EnableSseWanted](context: ActionAugments): void
    [ActionTypes.DisableSseWanted](context: ActionAugments): void
    [ActionTypes.EnableSseAutoConnect](context: ActionAugments): void
    [ActionTypes.DisableSseAutoConnect](context: ActionAugments): void
    [ActionTypes.ClearChatterState](context: ActionAugments): void

    // SSE
    [ActionTypes.InstallProfile](context: ActionAugments, chatter: Profile): void
    [ActionTypes.InstallChatter](context: ActionAugments, chatter: Chatter): void
    [ActionTypes.InstallConversation](context: ActionAugments, conversation: Conversation): void
    [ActionTypes.InstallConversationPart](context: ActionAugments, conversation: ConversationPart): void
    [ActionTypes.UpdateConversationAddMessage](context: ActionAugments, value: { conversationId: ConversationId, conversationMessageData: ConversationMessageData }): void
    [ActionTypes.UpdateConversationAddChatter](context: ActionAugments, value: { conversationId: ConversationId, conversationChatterData: ConversationChatterData }): void
    [ActionTypes.UpdateConversationMarkAsRead](context: ActionAugments, value: { conversationId: ConversationId, readMarkers: ReadMarkers }): void
    // USER
    [ActionTypes.SetJwt](context: ActionAugments, value: { jwt: string }): void
    [ActionTypes.SetChatterId](context: ActionAugments, value: { chatterId: ChatterId }): void
    [ActionTypes.SendMessage](context: ActionAugments, value: { chatterId: ChatterId, conversationId: ConversationId, message: string }): void
    [ActionTypes.MarkAsRead](context: ActionAugments, value: { chatterId: ChatterId, conversationId: ConversationId, conversationDataId: ConversationDataId }): void
    [ActionTypes.CreateConversation](context: ActionAugments, value: { chatterId: ChatterId, name: string, participants: ChatterId[], dialogue: boolean }): void
    [ActionTypes.InviteChatter](context: ActionAugments, value: { chatterId: ChatterId, conversationId: ConversationId, invitedChatterId: ChatterId }): void
}

// const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))
export const actions: ActionTree<State, State> & Actions = {
    async [ActionTypes.UpdateNavigatorOnlineStatus]({commit}, isOnline: boolean) {
        commit(MutationType.UpdateNavigatorOnlineStatus, isOnline)
    },
    async [ActionTypes.UpdateChatVisibility]({commit}, isVisible: boolean) {
        commit(MutationType.UpdateChatVisibility, isVisible)
    },
    async [ActionTypes.SetSelectedConversationId]({commit}, conversationId: ConversationId) {
        commit(MutationType.SetSelectedConversationId, conversationId)
        commit(MutationType.SetEditingProfile, false)
        commit(MutationType.SetCreatingConversation, false)
    },
    async [ActionTypes.DeselectConversationId]({commit}) {
        commit(MutationType.SetSelectedConversationId, undefined)
    },
    async [ActionTypes.SetEditingProfile]({commit}, enabled: boolean) {
        commit(MutationType.SetEditingProfile, enabled)
        commit(MutationType.SetCreatingConversation, false)
    },
    async [ActionTypes.SetCreatingConversation]({commit}, enabled: boolean) {
        commit(MutationType.SetCreatingConversation, enabled)
        commit(MutationType.SetEditingProfile, false)
    },
    async [ActionTypes.StoreSseOpenStatus]({commit}, isOpen: boolean) {
        commit(MutationType.UpdateSseOpenStatus, isOpen)
    },
    async [ActionTypes.StoreEventSource]({commit}, eventSource) {
        commit(MutationType.UpdateEventSource, eventSource)
    },
    async [ActionTypes.EnableSseWanted]({commit}) {
        commit(MutationType.SetSseWanted, true)
    },
    async [ActionTypes.DisableSseWanted]({commit}) {
        commit(MutationType.SetSseWanted, false)
    },
    async [ActionTypes.EnableSseAutoConnect]({commit}) {
        commit(MutationType.SetSseAutoConnect, true)
    },
    async [ActionTypes.ClearChatterState]({commit}) {
        commit(MutationType.ClearChatterState, undefined)
    },
    async [ActionTypes.DisableSseAutoConnect]({commit}) {
        commit(MutationType.SetSseAutoConnect, false)
    },
    async [ActionTypes.InstallProfile]({commit}, profile) {
        commit(MutationType.InstallProfile, profile)
    },
    async [ActionTypes.InstallChatter]({commit}, chatter) {
        commit(MutationType.InstallChatter, chatter)
    },
    async [ActionTypes.InstallConversation]({commit}, conversation) {
        commit(MutationType.InstallConversation, conversation)
    },
    async [ActionTypes.InstallConversationPart]({commit}, conversation) {
        commit(MutationType.InstallConversationPart, conversation)
    },
    async [ActionTypes.UpdateConversationAddMessage]({commit}, {conversationId, conversationMessageData}) {
        commit(MutationType.UpdateConversationAddMessage, {
            conversationId,
            conversationMessageData
        })
    },
    async [ActionTypes.UpdateConversationAddChatter]({commit}, {conversationId, conversationChatterData}) {
        commit(MutationType.UpdateConversationAddChatter, {
            conversationId,
            conversationChatterData
        })
    },
    async [ActionTypes.UpdateConversationMarkAsRead]({commit}, {conversationId, readMarkers}) {
        commit(MutationType.UpdateConversationMarkerAsRead, {conversationId, readMarkers})
    },
    async [ActionTypes.SetJwt]({commit}, {jwt}) {
        userApiClient.setJwt(jwt)
        chatApiClient.setJwt(jwt)
        commit(MutationType.SetJwt, jwt)
    },
    async [ActionTypes.SetChatterId]({commit}, {chatterId}) {
        commit(MutationType.SetChatterId, chatterId)
    },
    async [ActionTypes.SendMessage](context, {chatterId, conversationId, message}) {
        await chatApiClient.sendMessage(chatterId, conversationId, message)
    },
    async [ActionTypes.MarkAsRead](context, {chatterId, conversationId, conversationDataId}) {
        await chatApiClient.markAsRead(chatterId, conversationId, conversationDataId)
    },
    async [ActionTypes.CreateConversation](context, {chatterId, name, participants, dialogue}) {
        await chatApiClient.createConversation(chatterId, name, participants, dialogue)
    },
    async [ActionTypes.InviteChatter](context, {chatterId, conversationId, invitedChatterId}) {
        await chatApiClient.inviteChatter(chatterId, conversationId, invitedChatterId)
    },
}
