package com.pfplaybackend.api.config.websocket.domain.entity.data;

import com.pfplaybackend.api.config.websocket.domain.entity.domainmodel.PartyroomSession;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartyroomSessionData {
    private String sessionId;
    private UserId userId;
    private PartyroomId partyroomId;

    @Builder
    private PartyroomSessionData(String sessionId, UserId userId, PartyroomId partyroomId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.partyroomId = partyroomId;
    }

    public static PartyroomSessionData create(String sessionId, UserId userId, PartyroomId partyroomId) {
        return PartyroomSessionData.builder()
                .sessionId(sessionId)
                .userId(userId)
                .partyroomId(partyroomId)
                .build();
    }

    public PartyroomSession toDomain() {
        return PartyroomSession.builder()
                .sessionId(this.getSessionId())
                .userId(this.getUserId())
                .partyroomId(this.getPartyroomId())
                .build();
    }
}
