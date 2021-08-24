/* eslint-disable no-debugger */

import {Store} from "@/store";
import {EventSourcePolyfill} from "event-source-polyfill";
import {ActionTypes} from "@/store/actions";
import {ChatterId, Conversation, ConversationDataId, ConversationPart, ReadMarkers} from "@/store/state";

export default class SseChatService {

    private static toReadMarkers = (markedAsRead: ReadMarkers | undefined): Map<ChatterId, ConversationDataId> => {
        const readMarkers = new Map<ChatterId, ConversationDataId>();
        if (markedAsRead) {
            for (const [chatterId, conversationDataId] of Object.entries(markedAsRead)) {
                readMarkers.set(chatterId, conversationDataId)
            }
        }
        return readMarkers;
    }

    private static toConv = (conv: Partial<Conversation>): Conversation => {
        return {
            id: conv.id || "",
            creator: conv.creator || "",
            createdAt: conv.createdAt || 0,
            name: conv.name || "",
            data: conv.data || [],
            readMarkers: SseChatService.toReadMarkers(conv.readMarkers),
            dialogue: conv.dialogue || false,
            participants: conv.participants || []
        }
    }
    private static toConvPart = (conv: Partial<Conversation>): ConversationPart => {
        return {
            id: conv.id || "",
            data: conv.data || [],
            readMarkers: SseChatService.toReadMarkers(conv.readMarkers),
        }
    }

    static openSse(
        sseChatKey: string,
        onOpen: () => void,
        onReconnection: (e: { target: EventSource }) => void,
        onConnectionClosed: (e: { target: EventSource }) => void
    ): EventSource {
        const eventSource = new EventSourcePolyfill("http://localhost:8080/v1/chat/api/sse", {
            headers: {
                "sse-chat-key": sseChatKey
            }
        });
        eventSource.onopen = (/*e: Event*/) => {
            console.log('sse-chat-service> SSE is OPEN');
            onOpen()
        };
        eventSource.onmessage = (e: MessageEvent) => console.log('SSE message', JSON.parse(e.data));
        eventSource.onerror = (e: { target: EventSource }) => {
            console.log('sse-chat-service> SSE error', e)
            switch (e.target?.readyState) {

                case EventSource.CONNECTING:
                    console.log('sse-chat-service> es.onerror', e);
                    onReconnection(e)
                    break;

                case EventSource.CLOSED:
                    console.log('sse-chat-service> Connection failed, will not reconnect');
                    onConnectionClosed(e)
                    break;

            }
        };
        return eventSource;
    }

    static bind(eventSource: EventSource, store: Store): void {

        // eventSource.addEventListener('PROFILE:INSTALL', (e: any) => {
        //     const parse = JSON.parse(e.data);
        //     store.dispatch(ActionTypes.InstallProfile, parse.profile);
        // }, false);

        eventSource.addEventListener('CHATTER:INSTALL', (e: any) => {
            const parse = JSON.parse(e.data);
            if (parse.chatter?.id == store.state.chatterId) {
                store.dispatch(ActionTypes.InstallProfile, parse.chatter);
            }
            store.dispatch(ActionTypes.InstallChatter, parse.chatter);
        }, false);

        eventSource.addEventListener('CONVERS:INSTALL', (e: any) => {
            const parse = JSON.parse(e.data);
            const conv: Partial<Conversation> = parse.conversation;
            store.dispatch(ActionTypes.InstallConversation, SseChatService.toConv(conv));
        }, false);

        eventSource.addEventListener('CONVERS:PARTIAL', (e: any) => {
            const parse = JSON.parse(e.data);
            const conv: Partial<Conversation> = parse.conversation;
            store.dispatch(ActionTypes.InstallConversationPart, SseChatService.toConvPart(conv));
        }, false);

        eventSource.addEventListener('CONVERS:UPDATE:MESSAGE', (e: any) => {
            const parse = JSON.parse(e.data);
            store.dispatch(ActionTypes.UpdateConversationAddMessage, {
                conversationId: parse.conversation.id,
                conversationMessageData: parse.conversation.data[0]
            });
        }, false);

        eventSource.addEventListener('CONVERS:UPDATE:READ_MARKER', (e: any) => {
            const parse = JSON.parse(e.data);
            store.dispatch(ActionTypes.UpdateConversationMarkAsRead, {
                conversationId: parse.conversation.id,
                readMarkers: SseChatService.toReadMarkers(parse.conversation.readMarkers)
            });
        }, false);

        eventSource.addEventListener('CONVERS:UPDATE:ADD_CHATTER', (e: any) => {
            const parse = JSON.parse(e.data);
            store.dispatch(ActionTypes.UpdateConversationAddChatter, {
                conversationId: parse.conversation.id,
                conversationChatterData: parse.conversation.data[0]
            });
        }, false);

    }
}