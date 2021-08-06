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
    dateTime: number
    // others
} & Record<any, any>

export type ReadMarkers = Map<ChatterId, ConversationDataId>;

export type Conversation = {
    id: ConversationId,
    data: ConversationData[],
    readMarkers: ReadMarkers
    creator: ChatterId,
    name: string,
    createdAt: number
}

export type ConversationPart = {
    id: ConversationId,
    data: ConversationData[],
    readMarkers: ReadMarkers
}

export type Conversations = Map<ConversationId, Conversation>;

/** State */
export type State = {
    sse: Sse,
    chatterId?: ChatterId,
    chatters: Chatters,
    conversations: Conversations,
}