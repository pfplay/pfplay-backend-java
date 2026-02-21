package com.pfplaybackend.api.party.application.dto.result;

import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;

public record PartyroomSummaryResult(
        String title,
        String introduction,
        String linkDomain,
        int playbackTimeLimit,
        DjWithProfileDto currentDj
) {}
