import {ConversationDataId, ConversationId} from "@/store/state";

const debounceFetch = <Params extends never[]>(func: (...args: Params) => Promise<Response>, timeout = 300): (...args: Params) => void => {
    let timer: ReturnType<typeof setTimeout>;
    return (...args: Params) => {
        clearTimeout(timer)
        timer = setTimeout(() => func(...args), timeout)
    }
};

class ChatApiClient {

    private lastEmittedConversationDataIdPerConversationId = new Map<string, string>();
    private jwt = "";

    setJwt(jwt: string) {
        this.jwt = jwt
    }

    async initSse(conversationStatuses: () => Map<ConversationId, ConversationDataId>): Promise<string> {
        this.lastEmittedConversationDataIdPerConversationId.clear();
        return (await (await ChatApiClient.fetch(this.jwt, "init-sse", {
            conversationStatuses: conversationStatuses()
        })).json()).sseChatKey
    }

    async createConversation(userId: string, name: string, participants: string[], dialogue: boolean): Promise<void> {
        await ChatApiClient.fetch(this.jwt, "open", {
            name,
            participants,
            dialogue
        })
    }

    async sendMessage(userId: string, conversationId: string, message: string): Promise<void> {
        await ChatApiClient.fetch(this.jwt, "message", {
            conversationId,
            message
        })
    }

    async markAsRead(userId: string, conversationId: string, conversationDataId: string): Promise<void> {
        const lastEmittedConversationDataId = this.lastEmittedConversationDataIdPerConversationId.get(conversationId);
        if (lastEmittedConversationDataId === undefined || lastEmittedConversationDataId < conversationDataId) {
            this.lastEmittedConversationDataIdPerConversationId.set(conversationId, conversationDataId)
        }
        debounceFetch(() => ChatApiClient.fetch(this.jwt, "mark-as-read", {
                conversationId,
                conversationDataId
            }),
            1500
        )()
    }

    async inviteChatter(userId: string, conversationId: string, invitedChatterId: string): Promise<void> {
        await ChatApiClient.fetch(this.jwt, "invite-chatter", {
            conversationId,
            chatterId: invitedChatterId
        })
    }

    private static async fetch(jwt: string, endpoint: string, payload: unknown): Promise<Response> {
        return fetch("http://localhost:8080/v1/chat/api/" + endpoint, {
            method: 'POST',
            body: JSON.stringify(payload), // string or object
            headers: new Headers({
                "Authorization": 'Bearer ' + jwt,
                "Accept": "application/json",
                "Content-Type": "application/json",
            })
        })
    }

}

const chatApiClient = new ChatApiClient();

export {chatApiClient};
