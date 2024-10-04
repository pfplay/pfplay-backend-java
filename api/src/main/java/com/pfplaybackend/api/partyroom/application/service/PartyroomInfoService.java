package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.partyroom.application.dto.crew.CrewDto;
import com.pfplaybackend.api.partyroom.application.dto.crew.CrewSetupDto;
import com.pfplaybackend.api.partyroom.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.partyroom.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.partyroom.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.partyroom.domain.entity.converter.CrewConverter;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.CrewData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.partyroom.presentation.payload.response.info.QueryPartyroomSummaryResponse;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyroomInfoService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final UserProfilePeerService userProfileService;
    private final PlaybackInfoService playbackInfoService;

    public List<PartyroomWithCrewDto> getAllPartyrooms() {
        return partyroomRepository.getCrewDataByPartyroomId().stream().map(partyroomWithCrewDto -> {
            List<CrewDto> filteredCrews = partyroomWithCrewDto.getCrews().stream()
                                .filter(crewDto -> crewDto.getGradeType().isEqualOrHigherThan(GradeType.MODERATOR))
                                .limit(3)
                                .toList();
            return PartyroomWithCrewDto.from(partyroomWithCrewDto, filteredCrews);
        }).toList();
    }

    public Map<UserId, ProfileSettingDto> getPrimariesAvatarSettings(List<PartyroomWithCrewDto> partyrooms) {
        List<UserId> primaryUserIds = partyrooms.stream().collect(Collectors.toMap(
                        PartyroomWithCrewDto::getPartyroomId,
                        partyroomWithCrewDto -> partyroomWithCrewDto.getCrews().stream().toList()
                )).values().stream()
                .flatMap(List::stream)
                .map(CrewDto::getUserId).toList();
        return userProfileService.getUsersProfileSetting(primaryUserIds);
    }

    // 초기화를 위한 파티멤버 목록 조회
    public List<CrewSetupDto> getCrewsForSetup(PartyroomId partyroomId) {
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomDataDto partyroomDataDto = optional.get();
        PartyroomData partyroomData = partyroomConverter.toEntity(partyroomDataDto);
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);

        // FIXME Remove Field AuthorityTier, Uid
        Set<Crew> crews = partyroom.getCrewSet();
        List<UserId> userIds = crews.stream().map(Crew::getUserId).toList();
        Map<UserId, ProfileSettingDto> profileSettingMap = userProfileService.getUsersProfileSetting(userIds);

        return crews.stream().map(crew -> {
            UserId userId = crew.getUserId();
            return CrewSetupDto.from(crew, profileSettingMap.get(userId));
        }).toList();
    }

    // DjQueue 조회
    public void getDjQueueInfo(PartyroomId partyroomId) {
        // TODO Filter Deleted Djs
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
    }

    @Transactional
    public Partyroom getPartyroomById(PartyroomId partyroomId) {
        PartyroomData partyroomData = getPartyroom(partyroomId);
        return partyroomConverter.toDomain(partyroomData);
    }

    public boolean isAlreadyRegistered(Partyroom partyroom) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        return partyroom.getDjSet().stream().allMatch(dj -> dj.getUserId().equals(partyContext.getUserId()));
    }

    @Transactional
    public List<DjWithProfileDto> getDjs(Partyroom partyroom) {
        Set<Dj> djs = partyroom.getDjSet();
        List<UserId> userIds = djs.stream().map(Dj::getUserId).toList();
        Map<UserId, ProfileSettingDto> profileSettingMap = userProfileService.getUsersProfileSetting(userIds);

        return djs.stream().map(dj -> {
            UserId userId = dj.getUserId();
            ProfileSettingDto profileSettingDto = profileSettingMap.get(userId);
            return new DjWithProfileDto(
                    dj.getCrewId().getId(),
                    dj.getOrderNumber(),
                    profileSettingDto.getNickname(),
                    profileSettingDto.getAvatarIconUri()
            );
        }).toList();
    }

    public Optional<ActivePartyroomDto> getMyActivePartyroom() {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        return partyroomRepository.getActivePartyroomByUserId(partyContext.getUserId());
    }

    public Optional<ActivePartyroomDto> getMyActivePartyroom(UserId userId) {
        return partyroomRepository.getActivePartyroomByUserId(userId);
    }

    public Optional<ActivePartyroomWithCrewDto> getMyActivePartyroomWithCrewId(UserId userId) {
        return partyroomRepository.getMyActivePartyroomWithCrewIdByUserId(userId);
    }

    // TODO 우측 사이드 바의 두번째 탭 클릭 시 호출
    // 1. 전체 목록
    // 2. 제재 목록
    public void getCrews(PartyroomId partyroomId) {
        // TODO 내가 차단한 목록은 글로벌 수준으로 유지
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        List<UserId> crewUserIds = partyroomData.getCrewDataSet().stream().map(CrewData::getUserId).toList();
        Map<UserId, ProfileSettingDto> profileSettings = userProfileService.getUsersProfileSetting(crewUserIds);
    }

    public QueryPartyroomSummaryResponse getSummaryInfo(PartyroomId partyroomId) {
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        if(partyroom.isPlaybackActivated()) {
            // Extract Current Djs
            Playback playback = playbackInfoService.getPlaybackById(partyroom.getCurrentPlaybackId());
            Crew djCrew = partyroom.getCrewByUserId(playback.getUserId()).orElseThrow();
            UserId djUserId = djCrew.getUserId();
            ProfileSettingDto profileSettingDto = userProfileService.getUsersProfileSetting(Collections.singletonList(djUserId)).get(djUserId);
            return QueryPartyroomSummaryResponse.from(partyroom, djCrew, profileSettingDto);
        }else {
            return QueryPartyroomSummaryResponse.from(partyroom, null, null);
        }
    }

    @Transactional
    public Optional<Crew> getCrewByUserId(PartyroomId partyroomId, UserId userId) {
        Optional<PartyroomData> optional = partyroomRepository.findById(partyroomId.getId());
        if(optional.isPresent()) {
            PartyroomData partyroomData = optional.get();
            Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
            return partyroom.getCrewByUserId(userId);
        }else {
            return Optional.empty();
        }
    }

    @Transactional
    public PartyroomData getPartyroom(PartyroomId partyroomId) {
        // 두 개의 자녀 엔티티의 LEFT JOIN 이 필요하므로, 중복 제거를 위해서 QueryDSL 을 사용해야 한다.
        // PartyroomData partyroomData = partyroomRepository.findByPartyroomId(partyroomId.getId()).orElseThrow();
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        return partyroomConverter.toEntity(optional.get());

        // Projections 으로 읽어온 데이터(DTO)를 엔티티 객체로 변환해서 필드를 변경하지 않고
        // 저장한다면 updatedAt이 변동될까?
        // 변인 1. 영속성 컨텍스트에 엔티티가 없는 경우
        // System.out.println(partyroomDataDto);
        // partyroomRepository.save(partyroomData);

        // 변인 2. 영속성 컨텍스트에 엔티티가 이미 있는 경우

        // Dj 대기열에서 삭제된 크루를 조회에 계속 포함시켜야 할까?
        // Partyroom 에서 나간 크루를 조회에 계속 포함시켜야 할까?
        // └ 포함시켜야 하는 요건이 있을까?
    }
}
