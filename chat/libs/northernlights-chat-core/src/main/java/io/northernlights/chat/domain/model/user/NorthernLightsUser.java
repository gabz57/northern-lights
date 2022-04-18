package io.northernlights.chat.domain.model.user;

import io.northernlights.chat.domain.model.chatter.ChatterId;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class NorthernLightsUser {
    ChatterId chatterId;
    AuthOrigin externalOrigin;
    String externalUid;
}
