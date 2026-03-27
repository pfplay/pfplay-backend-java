package com.pfplaybackend.api.party.application.dto.playback;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record PlaybackHistoryDto(
        @JsonProperty("musicName") @Schema(example = "Never Gonna Give You Up") String trackName,
        @Schema(example = "DJ_Master") String nickname,
        @Schema(example = "https://cdn.pfplay.xyz/avatar/icon/1.png") String avatarIconUri
) {
}
