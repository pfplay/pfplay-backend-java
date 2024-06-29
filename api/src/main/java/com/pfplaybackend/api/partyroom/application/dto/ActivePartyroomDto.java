package com.pfplaybackend.api.partyroom.application.dto;

import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import lombok.Getter;

@Getter
public class ActivePartyroomDto {
    private Long id;
    private PlaybackId currentPlaybackId;
}
