/* eslint-disable no-debugger */

import {ConversationDataId, ConversationId} from "@/store/state";

class ChatApiClient {

    async authent(userId: string, conversationStatuses: () => Map<ConversationId, ConversationDataId>): Promise<string> {
        return (await (await ChatApiClient.fetch(userId, "auth", {
            conversationStatuses: conversationStatuses()
        })).json()).sseChatKey
    }

    async createConversation(userId: string, name: string, participants: string[], dialogue: boolean): Promise<void> {
        await ChatApiClient.fetch(userId, "open", {
            name,
            participants,
            dialogue
        })
    }

    async sendMessage(userId: string, conversationId: string, message: string): Promise<void> {
        await ChatApiClient.fetch(userId, "message", {
            conversationId,
            message
        })
    }

    async markAsRead(userId: string, conversationId: string, conversationDataId: string): Promise<void> {
        await ChatApiClient.fetch(userId, "mark-as-read", {
            conversationId,
            conversationDataId
        })
    }

    async inviteChatter(userId: string, conversationId: string, invitedChatterId: string): Promise<void> {
        await ChatApiClient.fetch(userId, "invite-chatter", {
            conversationId,
            chatterId: invitedChatterId
        })
    }

    private static async fetch(userId: string, endpoint: string, payload: unknown): Promise<Response> {
        return fetch("http://localhost:8080/v1/chat/api/" + endpoint, {
            method: 'POST',
            body: JSON.stringify(payload), // string or object
            headers: new Headers({
                "Authorization": 'Bearer ' + userId,
                "Accept": "application/json",
                "Content-Type": "application/json",
            })
        })
    }

}

const chatApiClient = new ChatApiClient();

export {chatApiClient};