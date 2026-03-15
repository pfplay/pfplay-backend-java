package com.pfplaybackend.api.party.application.dto.partyroom;

import io.swagger.v3.oas.annotations.media.Schema;

public record LinkEnterDto(
        @Schema(example = "1") long partyroomId,
        @Schema(example = "Friday Night Party") String title,
        @Schema(example = "Welcome to the party!") String introduction,
        PlaybackSummary playback,
        @Schema(example = "5") long crewCount
) {
    public record PlaybackSummary(
            @Schema(example = "Never Gonna Give You Up") String name,
            @Schema(example = "https://i.ytimg.com/vi/dQw4w9WgXcQ/default.jpg") String thumbnailImage
    ) {}
}
