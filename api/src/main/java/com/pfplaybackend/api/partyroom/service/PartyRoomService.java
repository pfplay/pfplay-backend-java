package com.pfplaybackend.api.partyroom.service;

import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.entity.PartyRoomJoin;
import com.pfplaybackend.api.partyroom.enums.PartyPermissionRole;
import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.exception.PartyRoomAccessException;
import com.pfplaybackend.api.partyroom.presentation.dto.*;
import com.pfplaybackend.api.partyroom.presentation.response.PartyRoomCreateAdminInfo;
import com.pfplaybackend.api.partyroom.presentation.response.PartyRoomCreateResponse;
import com.pfplaybackend.api.partyroom.repository.PartyPermissionRepository;
import com.pfplaybackend.api.partyroom.repository.PartyRoomJoinRepository;
import com.pfplaybackend.api.partyroom.repository.PartyRoomRepository;
import com.pfplaybackend.api.partyroom.repository.dsl.PartyRoomJoinRepositorySupport;
import com.pfplaybackend.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyRoomService {
    private final PartyRoomRepository partyRoomRepository;
    private final PartyRoomJoinRepository partyRoomJoinRepository;
    private final PartyPermissionRepository partyPermissionRepository;
    private final ObjectMapperConfig om;
    private final PartyRoomJoinRepositorySupport roomJoinRepositorySupport;

    @Transactional
    public PartyRoomCreateResponse createPartyRoom(PartyRoomCreateDto dto) {
        if(partyRoomRepository.findByDomain(dto.getDomain()).size() > 0) {
            throw new DuplicateKeyException("domain exists");
        }

        PartyRoom partyRoom = partyRoomRepository.findByUserId(dto.getUser().getId());
        PartyRoomPermissionDto partyRoomPermissionDefaultDto =
                om.mapper().convertValue(partyPermissionRepository.findByAuthority(PartyPermissionRole.ADMIN), PartyRoomPermissionDto.class);

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
                    .partyRoomPermission(partyRoomPermissionDefaultDto)
                    .isNew(false)
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
                .partyRoomPermission(partyRoomPermissionDefaultDto)
                .isNew(true)
                .build();
    }

    @Transactional
    public PartyRoomJoinResultDto join(Long roomId, JwtTokenInfo user) {
        PartyRoom partyRoom = partyRoomRepository.findById(roomId)
                .orElseThrow(NoSuchElementException::new);

        Optional<PartyRoomJoinResultDto> findByPartyRoomJoinDto =
                roomJoinRepositorySupport.findByRoomIdWhereJoinRoom(roomId, user.getUserId());

        // @TODO 킥 30초 제한 처리 필요
        if(findByPartyRoomJoinDto.isPresent()) {
            PartyRoomJoinResultDto dto = findByPartyRoomJoinDto.get();
            if (!Objects.isNull(dto.getPartyRoomBan()) ) {
                throw new PartyRoomAccessException("입장할 수 없습니다.");
            }

            if(partyRoom.getUser().getId().equals(user.getUserId()) || dto.isHasJoined()) {
                return dto;
            }

        }

        partyRoomJoinRepository.save(PartyRoomJoin.builder()
                .partyRoom(partyRoom)
                .partyRoomBan(null)
                .user(user.getUser())
                .active(PartyRoomStatus.ACTIVE)
                .role(PartyPermissionRole.LISTENER)
                .build()
        );

        return PartyRoomJoinResultDto.builder()
                .hasJoined(false)
                .partyRoom(om.mapper().convertValue(
                        partyRoom,
                        PartyRoomDto.class
                ))
                .partyRoomBan(null)
                .partyPermission(om.mapper().convertValue(
                        partyPermissionRepository.findByAuthority(PartyPermissionRole.LISTENER),
                        PartyPermissionDto.class
                ))
                .build();

    }

}
