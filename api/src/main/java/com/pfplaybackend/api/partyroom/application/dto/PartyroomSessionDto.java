package com.pfplaybackend.api.partyroom.application.dto;

import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Builder;
import lombok.Data;


@Data
public class PartyroomSessionDto {
    private String sessionId;
    private UserId userId;
    private PartyroomId partyroomId;
    private long memberId;

    @Builder
    private PartyroomSessionDto(String sessionId, UserId userId, PartyroomId partyroomId, long memberId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.partyroomId = partyroomId;
        this.memberId = memberId;
    }

    public static PartyroomSessionDto create(String sessionId, UserId userId, PartyroomId partyroomId, long memberId) {
        return PartyroomSessionDto.builder()
                .sessionId(sessionId)
                .userId(userId)
                .partyroomId(partyroomId)
                .memberId(memberId)
                .build();
    }
}
