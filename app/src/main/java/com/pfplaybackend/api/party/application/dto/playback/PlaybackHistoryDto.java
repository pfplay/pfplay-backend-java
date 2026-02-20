package com.pfplaybackend.api.party.application.dto.playback;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlaybackHistoryDto(
        @JsonProperty("musicName") String trackName,
        String nickname,
        String avatarIconUri
) {
}
