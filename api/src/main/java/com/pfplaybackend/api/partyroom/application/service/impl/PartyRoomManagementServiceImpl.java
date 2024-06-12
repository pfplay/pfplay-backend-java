package com.pfplaybackend.api.partyroom.application.service.impl;

import com.pfplaybackend.api.partyroom.application.service.PartyRoomManagementService;
import com.pfplaybackend.api.partyroom.domain.model.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.model.entity.domain.Partyroom;
import org.springframework.stereotype.Service;

@Service
public class PartyRoomManagementServiceImpl implements PartyRoomManagementService {

    public Partyroom createPartyRoom() {
        PartyroomData partyRoom = new PartyroomData();
        return partyRoom.toDomain();
    }
}
