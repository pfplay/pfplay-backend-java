package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.common.domain.value.Duration;

public record PlaybackTrackDto(
        String linkId,
        String name,
        String thumbnailImage,
        Duration duration,
        int orderNumber
) {
}
