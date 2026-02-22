package com.pfplaybackend.api.partyview.application.dto;

import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;

public record DisplayDto(
        boolean playbackActivated,
        PlaybackDto playback,
        ReactionDto reaction,
        CurrentDjDto currentDj
) {
}
