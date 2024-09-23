package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.*;
import com.pfplaybackend.api.partyroom.application.dto.active.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.active.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.PartyroomDataDto;
import com.pfplaybackend.api.partyroom.application.peer.UserProfilePeerService;
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
        Optional<PartyroomData> optPartyroomData = partyroomRepository.findByPartyroomId(partyroomId.getId());
        if(optPartyroomData.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomData partyroomData = optPartyroomData.get();
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);

        // Has uid, authorityTier
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
    public Partyroom getById(PartyroomId partyroomId) {
        PartyroomDataDto partyroomDataDto = partyroomRepository.findPartyroomDto(partyroomId);
        PartyroomData partyroomData = partyroomRepository.findByPartyroomId(partyroomId.getId()).orElseThrow();
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
                    dj.getId(),
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
}
