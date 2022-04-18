package io.northernlights.chat.domain.model.chatter;

import lombok.Value;

@Value
public class Chatter {
    ChatterId chatterID;
    String name;
    String picture;

    public Chatter(ChatterId chatterID, String name, String picture) {
        this.chatterID = chatterID;
        this.name = name;
        this.picture = picture;
    }

    public Chatter(ChatterId chatterID, String name) {
        this(chatterID, name, null);
    }
}
