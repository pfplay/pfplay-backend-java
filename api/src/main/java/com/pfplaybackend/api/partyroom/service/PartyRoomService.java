package com.pfplaybackend.api.partyroom.service;

import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.common.dto.PaginationDto;
import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.entity.PartyRoomJoin;
import com.pfplaybackend.api.enums.ExceptionEnum;
import com.pfplaybackend.api.partyroom.enums.PartyPermissionRole;
import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.exception.PartyRoomAccessException;
import com.pfplaybackend.api.partyroom.presentation.dto.*;
import com.pfplaybackend.api.partyroom.presentation.request.PartyRoomUpdateRequest;
import com.pfplaybackend.api.partyroom.presentation.response.PartyRoomCreateAdminInfo;
import com.pfplaybackend.api.partyroom.presentation.response.PartyRoomCreateResponse;
import com.pfplaybackend.api.partyroom.repository.PartyPermissionRepository;
import com.pfplaybackend.api.partyroom.repository.PartyRoomJoinRepository;
import com.pfplaybackend.api.partyroom.repository.PartyRoomRepository;
import com.pfplaybackend.api.partyroom.repository.dsl.PartyRoomJoinRepositorySupport;
import com.pfplaybackend.api.partyroom.repository.dsl.PartyRoomRepositorySupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
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
    private final PartyRoomRepositorySupport partyRoomRepositorySupport;

    @Transactional
    public PartyRoomCreateResponse createPartyRoom(
            final PartyRoomCreateDto dto
    ) {
        if(partyRoomRepository.findByDomain(dto.domainUrl()).size() > 0) {
            // 사용자 정의한 도메인일 때는 중복 처리
            if(!dto.isDomainOption()) {
                throw new DuplicateKeyException("이미 존재하는 도메인 주소입니다.");
            }

            // @TODO uuid가 겹칠 일이 있다면 추후 uuid 중복 체크 로직 추가하여 생성하는 로직 필요
        }

        PartyRoom partyRoom = partyRoomRepository.findByUserId(dto.getUser().getId());
        PartyRoomPermissionDto partyRoomPermissionDefaultDto =
                om.mapper().convertValue(
                        partyPermissionRepository.findByAuthority(PartyPermissionRole.ADMIN),
                        PartyRoomPermissionDto.class
                );

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
        // 파티룸 생성 시 join 테이블에 생성한 유저 추가
        partyRoomJoinRepository.save(PartyRoomJoin.builder()
                .partyRoom(partyRoom)
                .partyRoomBan(null)
                .user(dto.getUser())
                .active(PartyRoomStatus.ACTIVE)
                .role(PartyPermissionRole.ADMIN)
                .build()
        );
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
    public PartyRoomJoinResultDto join(
            final Long roomId,
            final JwtTokenInfo user
    ) {
        PartyRoom partyRoom = partyRoomRepository.findById(roomId)
                .orElseThrow(NoSuchElementException::new);

        long PartyRoomJoinTotalCount = partyRoomJoinRepository.countPartyRoomJoinByPartyRoomId(roomId);
        if(PartyRoomJoinTotalCount > 200) {
            throw new PartyRoomAccessException("정원이 초과된 파티룸이에요.");
        }

        Optional<PartyRoomJoinResultDto> findByPartyRoomJoinDto =
                roomJoinRepositorySupport.findByRoomIdWhereJoinRoom(roomId, user.getUserId());

        // @TODO 킥 30초 제한 처리 필요
        if(findByPartyRoomJoinDto.isPresent()) {
            PartyRoomJoinResultDto dto = findByPartyRoomJoinDto.get();
            if (!Objects.isNull(dto.getPartyRoomBan()) ) {
                throw new PartyRoomAccessException(
                        String.format("관리자에 의해 퇴출되셨습니다. \n 해당 파티룸 재입장이 불가합니다. 사유 %s", dto.getPartyRoomBan().getReason())
                );
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

    @Transactional
    public void updateInfo(
            final Long id,
            final JwtTokenInfo user,
            final PartyRoomUpdateRequest request
    ) {

        PartyRoom partyRoom = partyRoomRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("파티룸을 찾을 수 없습니다."));

        if(!partyRoom.getUser().getId().equals(user.getUserId())) {
            throw new AccessDeniedException(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getMessage());
        }

        partyRoom.updateInfo(request.getName(), request.getIntroduce(), request.getLimit());
    }

    @Transactional(readOnly = true)
    public PartyRoomHomeResultPaginationDto getPartyListAll(PageRequest pageRequest) {
        PageImpl<PartyRoomHomeResultDto> result = partyRoomRepositorySupport.findAll(pageRequest);
        return PartyRoomHomeResultPaginationDto.builder()
                .content(result.getContent())
                .pagination(
                        PaginationDto.builder()
                                .pageNumber(result.getPageable().getPageNumber())
                                .pageSize(result.getSize())
                                .totalPages(result.getTotalPages())
                                .totalElements(result.getTotalElements())
                                .hasNext(result.hasNext())
                                .build()
                )
                .build();
    }

}
