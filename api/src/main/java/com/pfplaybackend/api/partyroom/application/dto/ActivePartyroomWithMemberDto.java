package com.pfplaybackend.api.partyroom.application.dto;

import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActivePartyroomWithMemberDto {
    private Long id;
    private boolean isPlaybackActivated;
    private boolean isQueueClosed;
    private PlaybackId currentPlaybackId;
    private Long memberId;
}
