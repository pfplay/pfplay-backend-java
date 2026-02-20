package com.pfplaybackend.api.party.application.dto.playback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaybackHistoryDto {
    @JsonProperty("musicName")
    private String trackName;
    private String nickname;
    private String avatarIconUri;
}
