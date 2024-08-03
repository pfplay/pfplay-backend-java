package com.pfplaybackend.api.partyroom.application.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
public class DisplayDto {
    private boolean isPlaybackActivated;
    private PlaybackDto playback;
    private ReactionDto reaction;
    private CurrentDjDto currentDj;
}