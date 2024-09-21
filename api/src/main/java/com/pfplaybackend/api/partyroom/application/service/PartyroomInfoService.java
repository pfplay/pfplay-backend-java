package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.partyroom.application.aspect.PartyContextAspect;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.*;
import com.pfplaybackend.api.partyroom.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.CrewData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
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
    private final PartyContextAspect partyContextAspect;

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
        List<Crew> crews = partyroom.getCrews();
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
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        return partyroomConverter.toDomain(partyroomData);
    }

    public boolean isAlreadyRegistered(Partyroom partyroom) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        return partyroom.getDjs().stream().allMatch(dj -> dj.getUserId().equals(partyContext.getUserId()));
    }

    @Transactional
    public List<DjWithProfileDto> getDjs(Partyroom partyroom) {
        List<Dj> djs = partyroom.getDjs();
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

    public Optional<ActivePartyroomWithCrewDto> getMyActivePartyroomWithCrewId(UserId userId) {
        return partyroomRepository.getMyActivePartyroomWithCrewIdByUserId(userId);
    }

    public void getCrews(PartyroomId partyroomId) {}

    public void getSummaryInfo(PartyroomId partyroomId) {
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        // 파티원의 UserId 추출
        List<UserId> crewUserIds = partyroomData.getCrewDataList().stream().map(CrewData::getUserId).toList();
        Map<UserId, ProfileSettingDto> profileSettings = userProfileService.getUsersProfileSetting(crewUserIds);
        // TODO Combine Map
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
    public Optional<PartyroomId> getPartyroomId(UserId userId) {
        Optional<PartyroomIdDto> optional = partyroomRepository.getPartyroomDataWithUserId(userId);
        if(optional.isPresent()) {
            PartyroomId partyroomId = optional.get().getPartyroomId();
            return Optional.of(partyroomId);
        } else {
            return Optional.empty();
        }
    }
}
