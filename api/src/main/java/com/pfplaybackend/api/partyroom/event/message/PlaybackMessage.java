package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaybackMessage {
    private PartyroomId partyroomId;
}
