package com.pfplaybackend.api.partyroom.repository.custom;

import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.PartyroomDto;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.user.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface PartyroomRepositoryCustom {
    List<PartyroomDto> getAllPartyrooms();
    Optional<ActivePartyroomDto> getActivePartyroomByUserId(UserId userId);
}