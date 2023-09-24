package com.pfplaybackend.api.playlist.service;

import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyRoomCreateDto;
import com.pfplaybackend.api.partyroom.repository.PartyRoomRepository;
import com.pfplaybackend.api.user.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class PlayListService {
    private PartyRoomRepository partyRoomRepository;
    private UserRepository userRepository;

    public PlayListService(PartyRoomRepository partyRoomRepository, UserRepository userRepository) {
        this.partyRoomRepository = partyRoomRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PartyRoom createPartyRoom(PartyRoomCreateDto dto) {
        if(partyRoomRepository.findByDomain(dto.domainUrl()).size() > 0) {
            throw new DuplicateKeyException("domain exists");
        }

        PartyRoom partyRoom = partyRoomRepository.findByUserId(dto.getUser().getId());
        if(Objects.nonNull(partyRoom)) {
            return partyRoomRepository.findByUserId(partyRoom.getUser().getId());
        }

        return partyRoomRepository.save(dto.toEntity());
    }

}
