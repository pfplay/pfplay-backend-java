package com.pfplaybackend.api.partyroom.service;

import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyRoomCreateDto;
import com.pfplaybackend.api.partyroom.repository.PartyRoomRepository;
import org.springframework.dao.DuplicateKeyException;
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
        try {
            return partyRoomRepository.save(dto.toEntity());
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("Party room name is Duplicate", e);
        }
    }

}
