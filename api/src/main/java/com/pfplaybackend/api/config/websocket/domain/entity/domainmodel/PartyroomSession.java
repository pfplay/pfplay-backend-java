package com.pfplaybackend.api.config.websocket.domain.entity.domainmodel;

import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PartyroomSession {
    private String sessionId;
    private UserId userId;
    private PartyroomId partyroomId;

    @Builder
    public PartyroomSession(String sessionId, UserId userId, PartyroomId partyroomId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.partyroomId = partyroomId;
    }
}
