package com.pfplaybackend.api.partyroom.application.dto;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class PartyroomWithMemberDto {
    private final long partyroomId;
    private final StageType stageType;
    private final UserId hostId;
    private final String title;
    private final String introduction;
    private final boolean isPlaybackActivated;
    private final boolean isQueueClosed;
    private final Long memberCount;
    private final PlaybackDto playbackDto;
    private final List<PartymemberDto> members;

    public PartyroomWithMemberDto(Long partyroomId, StageType stageType, UserId hostId, String title, String introduction, boolean isPlaybackActivated, boolean isQueueClosed, Long memberCount, PlaybackDto playbackDto, List<PartymemberDto> members) {
        this.partyroomId = partyroomId;
        this.stageType = stageType;
        this.hostId = hostId;
        this.title = title;
        this.introduction = introduction;
        this.isPlaybackActivated = isPlaybackActivated;
        this.isQueueClosed = isQueueClosed;
        this.memberCount = memberCount;
        this.playbackDto = playbackDto;
        this.members = members;
    }

    static public PartyroomWithMemberDto from(PartyroomWithMemberDto dto, List<PartymemberDto> members) {
        return new PartyroomWithMemberDto(
                dto.getPartyroomId(),
                dto.getStageType(),
                dto.getHostId(),
                dto.getTitle(),
                dto.getIntroduction(),
                dto.isPlaybackActivated(),
                dto.isQueueClosed(),
                dto.getMemberCount(),
                dto.getPlaybackDto(),
                members
        );
    }
}