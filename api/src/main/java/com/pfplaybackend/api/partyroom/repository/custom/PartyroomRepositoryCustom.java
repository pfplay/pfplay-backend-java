package com.pfplaybackend.api.partyroom.repository.custom;

import com.pfplaybackend.api.partyroom.application.dto.*;
import com.pfplaybackend.api.user.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface PartyroomRepositoryCustom {
    List<PartyroomDto> getAllPartyrooms();
    Optional<ActivePartyroomDto> getActivePartyroomByUserId(UserId userId);
    Optional<ActivePartyroomWithCrewDto> getMyActivePartyroomWithCrewIdByUserId(UserId userId);
    List<PartyroomWithCrewDto> getCrewDataByPartyroomId();
    Optional<PartyroomIdDto> getPartyroomDataWithUserId(UserId userId);
}