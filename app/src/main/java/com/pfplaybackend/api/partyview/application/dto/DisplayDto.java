package com.pfplaybackend.api.partyview.application.dto;

import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;

public record DisplayDto(
        boolean isPlaybackActivated,
        PlaybackDto playback,
        ReactionDto reaction,
        CurrentDjDto currentDj
) {
}
