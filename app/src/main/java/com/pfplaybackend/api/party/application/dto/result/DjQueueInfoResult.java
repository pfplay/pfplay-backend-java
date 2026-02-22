package com.pfplaybackend.api.party.application.dto.result;

import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.QueueStatus;

import java.util.List;

public record DjQueueInfoResult(
        boolean playbackActivated,
        QueueStatus queueStatus,
        boolean registered,
        PlaybackData currentPlayback,
        List<DjWithProfileDto> djs
) {}
