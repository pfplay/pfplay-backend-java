package com.pfplaybackend.api.party.application.dto;

import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.domain.value.UserId;

import java.io.Serializable;

public record PlaybackDurationWaitPayload(
        PartyroomId partyroomId,
        UserId userId
) implements Serializable {}
