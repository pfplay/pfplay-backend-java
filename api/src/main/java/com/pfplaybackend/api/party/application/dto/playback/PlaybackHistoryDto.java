package com.pfplaybackend.api.party.application.dto.playback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaybackHistoryDto {
    private String musicName;
    private String nickname;
    private String avatarIconUri;
}

