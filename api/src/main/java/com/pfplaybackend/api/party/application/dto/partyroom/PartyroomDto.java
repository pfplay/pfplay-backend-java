package com.pfplaybackend.api.party.application.dto.partyroom;

import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.common.domain.value.UserId;

public record PartyroomDto(
        long partyroomId,
        StageType stageType,
        UserId hostId,
        String title,
        String introduction,
        boolean isPlaybackActivated,
        boolean isQueueClosed,
        long crewCount,
        PlaybackDto playbackDto
) {}
