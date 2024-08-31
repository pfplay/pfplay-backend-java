package com.pfplaybackend.api.partyroom.repository.custom;

import com.pfplaybackend.api.partyroom.application.dto.*;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomSessionData;
import com.pfplaybackend.api.user.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface PartyroomRepositoryCustom {
    List<PartyroomDto> getAllPartyrooms();
    Optional<ActivePartyroomDto> getActivePartyroomByUserId(UserId userId);
    Optional<ActivePartyroomWithMemberDto> getMyActivePartyroomWithMemberIdByUserId(UserId userId);
    List<PartyroomWithMemberDto> getMemberDataByPartyroomId();
    Optional<PartyroomIdDto> getPartyroomDataWithUserId(UserId userId);
    PartyroomSessionDto savePartyroomSession(PartyroomSessionDto partyroomSessionDto);
}