package com.pfplaybackend.api.partyview.application.dto;

import com.pfplaybackend.api.party.application.dto.playback.PlaybackDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record DisplayDto(
        @Schema(example = "true") boolean playbackActivated,
        PlaybackDto playback,
        ReactionDto reaction,
        CurrentDjDto currentDj
) {
}
