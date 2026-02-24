package com.pfplaybackend.api.party.application.dto.playback;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;

public record PlaybackDurationWaitDto(
        PartyroomId partyroomId,
        UserId userId
) implements Serializable {}
