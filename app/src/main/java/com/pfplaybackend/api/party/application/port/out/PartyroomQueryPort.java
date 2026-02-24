package com.pfplaybackend.api.party.application.port.out;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.util.List;
import java.util.Optional;

public interface PartyroomQueryPort {
    Optional<ActivePartyroomDto> getActivePartyroomByUserId(UserId userId);
    List<PartyroomWithCrewDto> getCrewDataByPartyroomId();
    List<PlaybackData> getRecentPlaybackHistory(PartyroomId partyroomId);
}
