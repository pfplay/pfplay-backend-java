package com.pfplaybackend.api.party.application.dto.playback;

import com.pfplaybackend.api.party.application.dto.dj.CurrentDjDto;

public record DisplayDto(
        boolean isPlaybackActivated,
        PlaybackDto playback,
        ReactionDto reaction,
        CurrentDjDto currentDj
) {
}
