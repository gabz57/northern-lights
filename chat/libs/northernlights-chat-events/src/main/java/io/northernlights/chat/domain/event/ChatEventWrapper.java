package io.northernlights.chat.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatEventWrapper {
    private final String eventId;
    private final Long eventTime;
    private final String type;
    private final ChatEvent event;
    private final String aggregateType;
    private final String aggregateId;
    //private final String teamId;

    @JsonCreator
    public ChatEventWrapper(
        @JsonProperty("event_id") String eventId,
        @JsonProperty("event_time") Long eventTime,
        @JsonProperty("type") String type,
        @JsonProperty("event") ChatEvent event,
        @JsonProperty("aggregate_type") String aggregateType,
        @JsonProperty("aggregate_id") String aggregateId
    ) {
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.type = type;
        this.event = event;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
    }

//    private ChatEventType type;
//
//    enum ChatEventType {
//        CONVERSATION,
//        FILE,
//        TEAM,
//        USER,
//    }
}
