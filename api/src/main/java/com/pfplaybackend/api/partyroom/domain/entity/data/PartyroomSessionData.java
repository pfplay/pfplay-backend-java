package com.pfplaybackend.api.partyroom.domain.entity.data;

import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
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
}
