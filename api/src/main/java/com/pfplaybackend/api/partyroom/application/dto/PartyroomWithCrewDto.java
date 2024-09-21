package com.pfplaybackend.api.partyroom.application.dto;

import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Getter;

import java.util.List;

@Getter
public class PartyroomWithCrewDto {
    private final long partyroomId;
    private final StageType stageType;
    private final UserId hostId;
    private final String title;
    private final String introduction;
    private final boolean isPlaybackActivated;
    private final boolean isQueueClosed;
    private final Long crewCount;
    private final PlaybackDto playbackDto;
    private final List<CrewDto> crews;

    public PartyroomWithCrewDto(Long partyroomId, StageType stageType, UserId hostId, String title, String introduction,
                                boolean isPlaybackActivated, boolean isQueueClosed, Long crewCount, PlaybackDto playbackDto, List<CrewDto> crews) {
        this.partyroomId = partyroomId;
        this.stageType = stageType;
        this.hostId = hostId;
        this.title = title;
        this.introduction = introduction;
        this.isPlaybackActivated = isPlaybackActivated;
        this.isQueueClosed = isQueueClosed;
        this.crewCount = crewCount;
        this.playbackDto = playbackDto;
        this.crews = crews;
    }

    static public PartyroomWithCrewDto from(PartyroomWithCrewDto dto, List<CrewDto> members) {
        return new PartyroomWithCrewDto(
                dto.getPartyroomId(),
                dto.getStageType(),
                dto.getHostId(),
                dto.getTitle(),
                dto.getIntroduction(),
                dto.isPlaybackActivated(),
                dto.isQueueClosed(),
                dto.getCrewCount(),
                dto.getPlaybackDto(),
                members
        );
    }
}