package com.pfplaybackend.api.partyroom.service;

import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyRoomCreateDto;
import com.pfplaybackend.api.partyroom.repository.PartyRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartyRoomService {

    private PartyRoomRepository partyRoomRepository;

    public PartyRoomService(PartyRoomRepository partyRoomRepository) {
        this.partyRoomRepository = partyRoomRepository;
    }

    @Transactional
    public PartyRoom createPartyRoom(PartyRoomCreateDto dto) {
        return partyRoomRepository.save(dto.toEntity());
    }

}
