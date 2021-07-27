import {Store} from "@/store";
// import {MutationType} from "@/store/mutations";
import {EventSourcePolyfill} from "event-source-polyfill";

class SseChatService {

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

    openSse(sseChatKey: string): EventSource {
        const eventSource = new EventSourcePolyfill("http://localhost:8080/v1/chat/api/sse", {
            headers: {
                "sse-chat-key": sseChatKey
            }
        });
        eventSource.onmessage = (e: any) => console.log('message', JSON.parse(e.data));
        return eventSource;
    }

    listen(eventSource: EventSource, store: Store) {

        eventSource.addEventListener('CHATTER:INSTALL', function (e: any) {
            console.log('CHATTER:INSTALL', e.data);
            // store.commit(MutationType.InstallChatter, chatter);
        }, false);

        eventSource.addEventListener('CONVERS:INSTALL', function (e: any) {
            console.log('CONVERS:INSTALL', e.data);
            // store.commit(MutationType.InstallConversation, conversation);
        }, false);

        eventSource.addEventListener('CONVERS:UPDATE:MESSAGE', function (e: any) {
            console.log('CONVERS:UPDATE:MESSAGE', e.data);
            // store.commit(MutationType.UpdateConversationAddMessage, {conversationId, conversationData});
        }, false);

        eventSource.addEventListener('CONVERS:UPDATE:READ_MARKER', function (e: any) {
            console.log('CONVERS:UPDATE:READ_MARKER', e.data);
            // store.commit(MutationType.UpdateConversationMarkerAsRead, {conversationId, chatterId, conversationDataId});
        }, false);

    }
}

const sseChatService = new SseChatService();

export {sseChatService};