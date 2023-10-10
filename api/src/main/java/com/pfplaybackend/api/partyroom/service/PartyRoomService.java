package com.pfplaybackend.api.partyroom.service;

import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.partyroom.enums.PartyPermissionRole;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyRoomCreateDto;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyRoomPermissionDefaultDto;
import com.pfplaybackend.api.partyroom.presentation.response.PartyRoomCreateAdminInfo;
import com.pfplaybackend.api.partyroom.presentation.response.PartyRoomCreateResponse;
import com.pfplaybackend.api.partyroom.repository.PartyPermissionRepository;
import com.pfplaybackend.api.partyroom.repository.PartyRoomRepository;
import com.pfplaybackend.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
public class PartyRoomService {

    private PartyRoomRepository partyRoomRepository;
    private UserRepository userRepository;
    private PartyPermissionRepository partyPermissionRepository;
    private ObjectMapperConfig om;

    public PartyRoomService(PartyRoomRepository partyRoomRepository,
                            UserRepository userRepository,
                            PartyPermissionRepository partyPermissionRepository,
                            ObjectMapperConfig om) {
        this.partyRoomRepository = partyRoomRepository;
        this.userRepository = userRepository;
        this.partyPermissionRepository = partyPermissionRepository;
        this.om = om;
    }

    @Transactional
    public PartyRoomCreateResponse createPartyRoom(PartyRoomCreateDto dto) {

        if(partyRoomRepository.findByDomain(dto.getDomain()).size() > 0) {
            throw new DuplicateKeyException("domain exists");
        }

        PartyRoom partyRoom = partyRoomRepository.findByUserId(dto.getUser().getId());
        PartyRoomPermissionDefaultDto partyRoomPermissionDefaultDto =
                om.mapper().convertValue(partyPermissionRepository.findByAuthority(PartyPermissionRole.ADMIN), PartyRoomPermissionDefaultDto.class);

        if(Objects.nonNull(partyRoom)) {
            return PartyRoomCreateResponse
                    .builder()
                    .id(partyRoom.getId())
                    .name(partyRoom.getName())
                    .introduce(partyRoom.getIntroduce())
                    .domain(partyRoom.getDomain())
                    .djingLimit(partyRoom.getDjingLimit())
                    .type(partyRoom.getType())
                    .status(partyRoom.getStatus())
                    .admin(PartyRoomCreateAdminInfo
                            .builder()
                            .profile(dto.getUser().getFaceUrl())
                            .userName(dto.getUser().getNickname())
                            .build())
                    .defaultPartyPermission(partyRoomPermissionDefaultDto)
                    .build();
        }

        partyRoom = partyRoomRepository.save(dto.toEntity());
        return PartyRoomCreateResponse
                .builder()
                .id(partyRoom.getId())
                .name(partyRoom.getName())
                .introduce(partyRoom.getIntroduce())
                .domain(partyRoom.getDomain())
                .djingLimit(partyRoom.getDjingLimit())
                .type(partyRoom.getType())
                .status(partyRoom.getStatus())
                .admin(PartyRoomCreateAdminInfo
                        .builder()
                        .profile(dto.getUser().getFaceUrl())
                        .userName(dto.getUser().getNickname())
                        .build())
                .defaultPartyPermission(partyRoomPermissionDefaultDto)
                .build();
    }

}
