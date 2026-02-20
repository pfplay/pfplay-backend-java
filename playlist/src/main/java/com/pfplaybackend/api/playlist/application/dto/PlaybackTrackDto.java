package com.pfplaybackend.api.playlist.application.dto;

public record PlaybackTrackDto(
        String linkId,
        String name,
        String thumbnailImage,
        String duration,
        int orderNumber
) {
}
