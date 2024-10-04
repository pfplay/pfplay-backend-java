package com.pfplaybackend.api.partyroom.application.dto.playback;

import com.pfplaybackend.api.partyroom.application.dto.dj.CurrentDjDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DisplayDto {
    private boolean isPlaybackActivated;
    private PlaybackDto playback;
    private ReactionDto reaction;
    private CurrentDjDto currentDj;
}