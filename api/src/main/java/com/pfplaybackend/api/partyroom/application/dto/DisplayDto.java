package com.pfplaybackend.api.partyroom.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DisplayDto {
    private boolean isPlaybackActivated;
    private PlaybackDto playback;
    private ReactionDto reaction;
}
