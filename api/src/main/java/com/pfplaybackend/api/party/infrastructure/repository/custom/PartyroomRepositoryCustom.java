package com.pfplaybackend.api.party.infrastructure.repository.custom;

import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface PartyroomRepositoryCustom {
    Optional<ActivePartyroomDto> getActivePartyroomByUserId(UserId userId);
    Optional<ActivePartyroomWithCrewDto> getMyActivePartyroomWithCrewIdByUserId(UserId userId);
    List<PartyroomWithCrewDto> getCrewDataByPartyroomId();
    List<PlaybackData> getRecentPlaybackHistory(PartyroomId partyroomId);
    List<PartyroomData> findAllUnusedPartyroomDataByDay(int days);
}