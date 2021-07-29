/** Sse */
export type Sse = {
    eventSource?: EventSource
    sseOpen: boolean
    sseWanted: boolean
    sseAutoConnect: boolean
}

/** Chatter */
export type ChatterId = string

export type Chatter = {
    id: ChatterId,
    name: string
}

export type Chatters = Map<ChatterId, Chatter>;

/** Conversations */
export type ConversationId = string
export type ConversationDataId = string

export type ConversationData = {
    id: ConversationDataId
    author: ChatterId
    message: string
    // others
} & Record<any, any>

export type MarkedAsRead = Map<ChatterId, ConversationDataId>;

export type Conversation = {
    id: ConversationId,
    name: string,
    data: ConversationData[],
    markedAsRead: MarkedAsRead
}

export type Conversations = Map<ConversationId, Conversation>;

/** State */
export type State = {
    sse: Sse,
    chatterId?: ChatterId,
    chatters: Chatters,
    conversations: Conversations,
}