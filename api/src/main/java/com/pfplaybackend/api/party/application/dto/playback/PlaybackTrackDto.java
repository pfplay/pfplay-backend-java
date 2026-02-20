package com.pfplaybackend.api.party.application.dto.playback;

public record PlaybackTrackDto(
        String linkId,
        String name,
        String thumbnailImage,
        String duration,
        int orderNumber
) {
}
