package com.pfplaybackend.api.party.adapter.in.web.payload.response.access;

public record LinkEnterResponse(
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
