/* eslint-disable no-debugger */

import {Store} from "@/store";
import {EventSourcePolyfill} from "event-source-polyfill";
import {ActionTypes} from "@/store/actions";
import {ChatterId, Conversation, ConversationDataId, MarkedAsRead} from "@/store/state";

export default class SseChatService {

    private static toReadMarkers = (markedAsRead: MarkedAsRead | undefined): Map<ChatterId, ConversationDataId> => {
        const readMarkers = new Map<ChatterId, ConversationDataId>();
        if (markedAsRead) {
            markedAsRead.forEach((conversationDataId, chatterId) => readMarkers.set(chatterId, conversationDataId))
        }
        return readMarkers;
    }

    private static toConv = (conv: Partial<Conversation>): Conversation => {
        return {
            id: conv.id || "",
            name: conv.name || "",
            data: conv.data || [],
            markedAsRead: SseChatService.toReadMarkers(conv.markedAsRead),
        }
    }

    static openSse(sseChatKey: string, onOpen: () => void, onReconnection: () => void, onConnectionClosed: () => void): EventSource {
        const eventSource = new EventSourcePolyfill("http://localhost:8080/v1/chat/api/sse", {
            headers: {
                "sse-chat-key": sseChatKey
            }
        });
        eventSource.onopen = (e: Event) => {
            console.log('SSE is OPEN');
            onOpen()
        };
        eventSource.onmessage = (e: MessageEvent) => console.log('SSE message', JSON.parse(e.data));
        eventSource.onerror = (e: any) => {
            console.log('SSE error', e)
            switch (e.target.readyState) {

                case EventSource.CONNECTING:
                    console.log('Reconnecting...');
                    onReconnection()
                    break;

                case EventSource.CLOSED:
                    console.log('Connection failed, will not reconnect');
                    onConnectionClosed()
                    break;

            }
        };
        return eventSource;
    }

    static bind(eventSource: EventSource, store: Store) {

        eventSource.addEventListener('CHATTER:INSTALL', (e: any) => {
            const parse = JSON.parse(e.data);
            store.dispatch(ActionTypes.InstallChatter, parse.chatter);
        }, false);

        eventSource.addEventListener('CONVERS:INSTALL', (e: any) => {
            const parse = JSON.parse(e.data);
            const conv: Partial<Conversation> = parse.conversation;
            store.dispatch(ActionTypes.InstallConversation, SseChatService.toConv(conv));
        }, false);

        eventSource.addEventListener('CONVERS:UPDATE:MESSAGE', (e: any) => {
            const parse = JSON.parse(e.data);
            store.dispatch(ActionTypes.UpdateConversationAddMessage, {
                conversationId: parse.conversation.id,
                conversationData: parse.conversation.data
            });
        }, false);

        eventSource.addEventListener('CONVERS:UPDATE:READ_MARKER', (e: any) => {
            const parse = JSON.parse(e.data);
            store.dispatch(ActionTypes.UpdateConversationMarkAsRead, {
                conversationId: parse.conversation.id,
                readMarkers: parse.conversation.markedAsRead
            });
        }, false);

    }
}