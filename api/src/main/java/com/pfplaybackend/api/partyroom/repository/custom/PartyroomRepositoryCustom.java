package com.pfplaybackend.api.partyroom.repository.custom;

import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomWithMemberDto;
import com.pfplaybackend.api.partyroom.application.dto.PartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.PartyroomWithMemberDto;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.user.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface PartyroomRepositoryCustom {
    List<PartyroomDto> getAllPartyrooms();
    Optional<ActivePartyroomDto> getActivePartyroomByUserId(UserId userId);
    Optional<ActivePartyroomWithMemberDto> getMyActivePartyroomWithMemberIdByUserId(UserId userId);
    List<PartyroomWithMemberDto> getMemberDataByPartyroomId();
}