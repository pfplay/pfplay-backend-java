package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.application.dto.playback.PlaybackDto;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaybackStartMessage implements Serializable {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private long crewId;
    private PlaybackDto playback;
}