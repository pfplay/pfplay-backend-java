package com.pfplaybackend.api.playlist.application.dto;

import com.pfplaybackend.api.common.domain.value.Duration;

public record PlaylistTrackDto(
        Long trackId,
        String linkId,
        String name,
        Integer orderNumber,
        Duration duration,
        String thumbnailImage
) {}
