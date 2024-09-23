package com.pfplaybackend.api.partyroom.repository.custom;

import com.pfplaybackend.api.partyroom.application.dto.*;
import com.pfplaybackend.api.partyroom.application.dto.active.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.active.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.PartyroomDataDto;
import com.pfplaybackend.api.partyroom.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface PartyroomRepositoryCustom {
    Optional<ActivePartyroomDto> getActivePartyroomByUserId(UserId userId);
    Optional<ActivePartyroomWithCrewDto> getMyActivePartyroomWithCrewIdByUserId(UserId userId);
    List<PartyroomWithCrewDto> getCrewDataByPartyroomId();
    List<PlaybackData> getRecentPlaybackHistory(PartyroomId partyroomId);
    PartyroomDataDto findPartyroomDto(PartyroomId partyroomId);
}