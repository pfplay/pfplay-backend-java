package com.pfplaybackend.api.partyroom.application.service.impl;

import com.pfplaybackend.api.partyroom.application.service.PartyRoomManagementService;
import com.pfplaybackend.api.partyroom.domain.model.entity.domain.PartyroomDomain;
import com.pfplaybackend.api.partyroom.domain.model.entity.data.Partyroom;
import org.springframework.stereotype.Service;

@Service
public class PartyRoomManagementServiceImpl implements PartyRoomManagementService {

    public PartyroomDomain createPartyRoom() {
        Partyroom partyRoom = new Partyroom();
        return partyRoom.toDomain();
    }
}
