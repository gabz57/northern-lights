
/** UI */
export type Ui = {
    online: boolean
    visible: boolean
    selectedConversationId?: ConversationId
    creatingConversation: boolean
    editingProfile: boolean
}

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


/** Profile */
export type Profile = Chatter

/** Conversations */
export type ConversationId = string
export type ConversationDataId = string

export type ConversationData = ConversationMessageData | ConversationChatterData

export type ConversationDataValue = {
    id: ConversationDataId
    type: string
    dateTime: number
    from: ChatterId
}

export type ConversationMessageData = ConversationDataValue & {
    message: string
}

export type ConversationChatterData = ConversationDataValue & {
    chatterId: ChatterId
}

export type ReadMarkers = Map<ChatterId, ConversationDataId>;

export type Conversation = {
    id: ConversationId,
    name: string,
    creator: ChatterId,
    createdAt: number,
    dialogue: boolean,
    participants: ChatterId[],
    data: ConversationData[],
    readMarkers: ReadMarkers
}

export type ConversationPart = {
    id: ConversationId,
    data: ConversationData[],
    readMarkers: ReadMarkers
}

export type Conversations = Map<ConversationId, Conversation>;

/** Model */
export type Model = {
    ui: Ui,
    sse: Sse,
    jwt: string,
    chatterId?: ChatterId,
    profile?: Profile,
    chatters: Chatters,
    conversations: Conversations,
}
