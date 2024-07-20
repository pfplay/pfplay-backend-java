package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.application.dto.DjDto;
import com.pfplaybackend.api.partyroom.application.dto.PlaybackDto;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaybackMessage {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private long memberId;
    private PlaybackDto playback;
}
