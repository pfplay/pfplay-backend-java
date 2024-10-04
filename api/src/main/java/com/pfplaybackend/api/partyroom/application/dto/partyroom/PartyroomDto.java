package com.pfplaybackend.api.partyroom.application.dto.partyroom;

import com.pfplaybackend.api.partyroom.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.partyroom.domain.enums.StageType;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PartyroomDto {
    private long partyroomId;
    private StageType stageType;
    private UserId hostId;
    private String title;
    private String introduction;
    private boolean isPlaybackActivated;
    private boolean isQueueClosed;
    private long crewCount;
    private PlaybackDto playbackDto;
}