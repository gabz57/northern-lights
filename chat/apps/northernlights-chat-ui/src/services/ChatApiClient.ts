/* eslint-disable no-debugger */

class ChatApiClient {

    async authent(userId: string): Promise<string> {
        const myHeaders = new Headers();
        myHeaders.append("Authorization", 'Bearer ' + userId);
        myHeaders.append("Accept", "application/json");
        myHeaders.append("Content-Type", "application/json");
        const response = await fetch("http://localhost:8080/v1/chat/api/auth", {
            method: 'POST',
            body: JSON.stringify({
                'conversationStatuses': {}
            }), // string or object
            headers: myHeaders
        });
        return (await response.json()).sseChatKey;
    }

    async createConversation(userId: string, name: string, participants: string[]): Promise<void> {
        const myHeaders = new Headers();
        myHeaders.append("Authorization", 'Bearer ' + userId);
        myHeaders.append("Accept", "application/json");
        myHeaders.append("Content-Type", "application/json");
        await fetch("http://localhost:8080/v1/chat/api/open", {
            method: 'POST',
            body: JSON.stringify({
                name,
                participants
            }), // string or object
            headers: myHeaders
        });

    }

    async sendMessage(userId: string, conversationId: string, message: string): Promise<void> {
        const myHeaders = new Headers();
        myHeaders.append("Authorization", 'Bearer ' + userId);
        myHeaders.append("Accept", "application/json");
        myHeaders.append("Content-Type", "application/json");
        await fetch("http://localhost:8080/v1/chat/api/message", {
            method: 'POST',
            body: JSON.stringify({
                conversationId,
                message
            }), // string or object
            headers: myHeaders
        });
    }

    async markAsRead(userId: string, conversationId: string, conversationDataId: string): Promise<void> {
        const myHeaders = new Headers();
        myHeaders.append("Authorization", 'Bearer ' + userId);
        myHeaders.append("Accept", "application/json");
        myHeaders.append("Content-Type", "application/json");
        await fetch("http://localhost:8080/v1/chat/api/mark-as-read", {
            method: 'POST',
            body: JSON.stringify({
                conversationId,
                conversationDataId
            }), // string or object
            headers: myHeaders
        });
    }

}

const chatApiClient = new ChatApiClient();

export {chatApiClient};