package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.model.entity.domain.PartyroomDomain;
import org.springframework.stereotype.Service;

public interface PartyRoomManagementService {
    public PartyroomDomain createPartyRoom();
}
