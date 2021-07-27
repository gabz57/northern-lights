/** Chatter */
export type ChatterId = string

export type Chatter = {
    id: ChatterId,
    name: string
}

export type Chatters = {
    [type: string]: Chatter
}

/** Conversations */
export type ConversationId = string
export type ConversationDataId = string

export type ConversationData = {
    id: ConversationDataId
    // others
} & Record<any, any>

export type MarkedAsRead = {
    [type: string]: ConversationDataId
}

export type Conversation = {
    id: ConversationId,
    name: string,
    events: [ConversationData],
    markedAsRead: MarkedAsRead
}

export type Conversations = {
    [type: string]: Conversation
}

/** State */
export type State = {
    chatterId: ChatterId,
    chatters: Chatters,
    conversations: Conversations,
}