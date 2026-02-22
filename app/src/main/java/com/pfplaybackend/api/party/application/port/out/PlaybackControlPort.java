package com.pfplaybackend.api.party.application.port.out;

import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

public interface PlaybackControlPort {
    void startPlayback(PartyroomData partyroom);
    void skipPlayback(PartyroomId partyroomId);
}
