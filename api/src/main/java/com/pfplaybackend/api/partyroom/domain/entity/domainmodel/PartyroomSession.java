package com.pfplaybackend.api.partyroom.domain.entity.domainmodel;

import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PartyroomSession {
    private long id;
    private String sessionId;
    private UserId userId;
    private PartyroomId partyroomId;

    @Builder
    public PartyroomSession(long id, String sessionId, UserId userId, PartyroomId partyroomId) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.partyroomId = partyroomId;
    }
}
