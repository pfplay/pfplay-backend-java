package com.pfplaybackend.api.party.application.dto.partyroom;

import com.pfplaybackend.api.party.domain.value.PlaybackId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActivePartyroomDto {
    private Long id;
    private boolean isPlaybackActivated;
    private boolean isQueueClosed;
    private PlaybackId currentPlaybackId;
}
