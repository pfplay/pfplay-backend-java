package com.pfplaybackend.api.partyroom.application.dto.base;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PartyroomDataDto {
    private Long id;
    private String title;
    private String introduction;
    private PartyroomId partyroomId;
    private StageType stageType;
    private String linkDomain;
    private int playbackTimeLimit;
    private UserId hostId;
    private String noticeContent;
    private PlaybackId currentPlaybackId;
    private boolean isPlaybackActivated;
    private boolean isQueueClosed;
    private boolean isTerminated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<CrewDataDto> crewDataSet;
    private Set<DjDataDto> djDataSet;

    public PartyroomDataDto(Long id, String title, String introduction, PartyroomId partyroomId, StageType stageType,
                            String linkDomain, int playbackTimeLimit, UserId hostId, String noticeContent, PlaybackId currentPlaybackId, boolean isPlaybackActivated,
                            boolean isQueueClosed, boolean isTerminated, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.introduction = introduction;
        this.partyroomId = partyroomId;
        this.stageType = stageType;
        this.linkDomain = linkDomain;
        this.playbackTimeLimit = playbackTimeLimit;
        this.hostId = hostId;
        this.noticeContent = noticeContent;
        this.currentPlaybackId = currentPlaybackId;
        this.isPlaybackActivated = isPlaybackActivated;
        this.isQueueClosed = isQueueClosed;
        this.isTerminated = isTerminated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.crewDataSet = new HashSet<>();
        this.djDataSet = new HashSet<>();
    }

    public static PartyroomDataDto create(PartyroomData partyroomData) {
        return new PartyroomDataDto(
                partyroomData.getId(),
                partyroomData.getTitle(),
                partyroomData.getIntroduction(),
                partyroomData.getPartyroomId(),
                partyroomData.getStageType(),
                partyroomData.getLinkDomain(),
                partyroomData.getPlaybackTimeLimit(),
                partyroomData.getHostId(),
                partyroomData.getNoticeContent(),
                partyroomData.getCurrentPlaybackId(),
                partyroomData.isPlaybackActivated(),
                partyroomData.isQueueClosed(),
                partyroomData.isTerminated(),
                partyroomData.getCreatedAt(),
                partyroomData.getUpdatedAt()
        );
    }
}
