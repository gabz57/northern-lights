import {ActionContext, ActionTree} from 'vuex'
import {Mutations, MutationType} from './mutations'
import {
    Chatter,
    ChatterId,
    Conversation,
    ConversationData,
    ConversationDataId,
    ConversationId, ConversationPart,
    ReadMarkers,
    State
} from './state'
import {chatApiClient} from "@/services/ChatApiClient";

export enum ActionTypes {
    // SSE Mngt
    StoreSseOpenStatus = "STORE_SSE_OPEN_STATUS",
    StoreEventSource = "STORE_EVENT_SOURCE",
    EnableSseWanted = "ENABLE_SSE",
    DisableSseWanted = "DISABLE_SSE",
    EnableSseAutoConnect = "ENABLE_SSE_AUTO_CONNECT",
    DisableSseAutoConnect = "DISABLE_SSE_AUTO_CONNECT",
    ClearChatterState = "CLEAR_CHATTER_STATE",
    // SSE
    InstallChatter = 'INSTALL_CHATTER',
    InstallConversation = 'INSTALL_CONVERSATION',
    InstallConversationPart = 'INSTALL_CONVERSATION_PART',
    UpdateConversationAddMessage = 'UPDATE_CONVERSATION_ADD_EVENT',
    UpdateConversationMarkAsRead = 'UPDATE_CONVERSATION_MARKER_AS_READ',
    // USER
    SetChatterId = 'SET_CHATTER_ID',
    SendMessage = 'SEND_MESSAGE',
    MarkAsRead = 'MARK_AS_READ'
}

type ActionAugments = Omit<ActionContext<State, State>, 'commit'> & {
    commit<K extends keyof Mutations>(
        key: K,
        payload: Parameters<Mutations[K]>[1]
    ): ReturnType<Mutations[K]>
}
export type Actions = {
    // SSE Mngt
    [ActionTypes.StoreSseOpenStatus](context: ActionAugments, isOpen: boolean): void
    [ActionTypes.StoreEventSource](context: ActionAugments, eventSource: EventSource): void
    [ActionTypes.EnableSseWanted](context: ActionAugments): void
    [ActionTypes.DisableSseWanted](context: ActionAugments): void
    [ActionTypes.EnableSseAutoConnect](context: ActionAugments): void
    [ActionTypes.DisableSseAutoConnect](context: ActionAugments): void
    [ActionTypes.ClearChatterState](context: ActionAugments): void

    // SSE
    [ActionTypes.InstallChatter](context: ActionAugments, chatter: Chatter): void
    [ActionTypes.InstallConversation](context: ActionAugments, conversation: Conversation): void
    [ActionTypes.InstallConversationPart](context: ActionAugments, conversation: ConversationPart): void
    [ActionTypes.UpdateConversationAddMessage](context: ActionAugments, value: { conversationId: ConversationId, conversationData: ConversationData[] }): void
    [ActionTypes.UpdateConversationMarkAsRead](context: ActionAugments, value: { conversationId: ConversationId, readMarkers: ReadMarkers }): void
    // USER
    [ActionTypes.SetChatterId](context: ActionAugments, value: { chatterId: ChatterId }): void
    [ActionTypes.SendMessage](context: ActionAugments, value: { chatterId: ChatterId, conversationId: ConversationId, message: string }): void
    [ActionTypes.MarkAsRead](context: ActionAugments, value: { chatterId: ChatterId, conversationId: ConversationId, conversationDataId: ConversationDataId }): void
}

// const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))
export const actions: ActionTree<State, State> & Actions = {
    async [ActionTypes.StoreSseOpenStatus]({commit}, isOpen: boolean) {
        console.log("ActionTypes.StoreSseOpenStatus")
        commit(MutationType.UpdateSseOpenStatus, isOpen)
    },
    async [ActionTypes.StoreEventSource]({commit}, eventSource) {
        console.log("ActionTypes.StoreEventSource")
        commit(MutationType.UpdateEventSource, eventSource)
    },
    async [ActionTypes.EnableSseWanted]({commit}) {
        console.log("ActionTypes.EnableSseWanted")
        commit(MutationType.SetSseWanted, true)
    },
    async [ActionTypes.DisableSseWanted]({commit}) {
        console.log("ActionTypes.DisableSseWanted")
        commit(MutationType.SetSseWanted, false)
    },
    async [ActionTypes.EnableSseAutoConnect]({commit}, ) {
        commit(MutationType.SetSseAutoConnect, true)
    },
    async [ActionTypes.ClearChatterState]({commit}) {
        commit(MutationType.ClearChatterState, undefined)
    },
    async [ActionTypes.DisableSseAutoConnect]({commit}) {
        commit(MutationType.SetSseAutoConnect, false)
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
    async [ActionTypes.UpdateConversationAddMessage]({commit}, {conversationId, conversationData}) {
        commit(MutationType.UpdateConversationAddMessage, {
            conversationId,
            conversationData
        })
    },
    async [ActionTypes.UpdateConversationMarkAsRead]({commit}, {conversationId, readMarkers}) {
        commit(MutationType.UpdateConversationMarkerAsRead, {conversationId, readMarkers})
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
}