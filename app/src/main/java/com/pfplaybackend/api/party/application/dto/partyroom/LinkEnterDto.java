package com.pfplaybackend.api.party.application.dto.partyroom;

public record LinkEnterDto(
        long partyroomId,
        String title,
        String introduction,
        PlaybackSummary playback,
        long crewCount
) {
    public record PlaybackSummary(
            String name,
            String thumbnailImage
    ) {}
}
