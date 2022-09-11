package io.northernlights.chat.domain.model.chatter;

import lombok.Value;

@Value
public class Chatter {
    ChatterId chatterId;
    String name;
    String picture;

    public Chatter(ChatterId chatterId, String name, String picture) {
        this.chatterId = chatterId;
        this.name = name;
        this.picture = picture;
    }

    public Chatter(ChatterId chatterId, String name) {
        this(chatterId, name, null);
    }
}
