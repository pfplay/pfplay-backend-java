package com.pfplaybackend.api.admin.adapter.in.web.dto.response;

import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Response DTO for admin partyroom operations
 */
@Getter
@Builder
@AllArgsConstructor
public class AdminPartyroomResponse {

    private Long partyroomId;
    private String hostUserId;
    private String title;
    private String introduction;
    private String linkDomain;
    private Integer playbackTimeLimit;
    private String stageType;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public static AdminPartyroomResponse from(PartyroomData partyroom, String hostUserId) {
        return AdminPartyroomResponse.builder()
                .partyroomId(partyroom.getPartyroomId().getId())
                .hostUserId(hostUserId)
                .title(partyroom.getTitle())
                .introduction(partyroom.getIntroduction())
                .linkDomain(partyroom.getLinkDomain())
                .playbackTimeLimit(partyroom.getPlaybackTimeLimit())
                .stageType(partyroom.getStageType().name())
                .isActive(!partyroom.isTerminated())
                .createdAt(partyroom.getCreatedAt())
                .build();
    }
}
