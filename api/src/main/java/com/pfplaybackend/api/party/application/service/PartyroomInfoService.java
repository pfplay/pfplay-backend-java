package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.crew.CrewDto;
import com.pfplaybackend.api.party.application.dto.crew.CrewSetupDto;
import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.response.info.QueryPartyroomSummaryResponse;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
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
    @Transactional(readOnly = true)
    public List<CrewSetupDto> getCrewsForSetup(PartyroomId partyroomId) {
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

        Set<CrewData> crews = partyroom.getActiveCrewDataSet();
        List<UserId> userIds = crews.stream().map(CrewData::getUserId).toList();
        Map<UserId, ProfileSettingDto> profileSettingMap = userProfileService.getUsersProfileSetting(userIds);

        return crews.stream().map(crew -> {
            UserId userId = crew.getUserId();
            return CrewSetupDto.from(crew, profileSettingMap.get(userId));
        }).toList();
    }

    // DjQueue 조회
    public void getDjQueueInfo(PartyroomId partyroomId) {
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
    }

    @Transactional(readOnly = true)
    public PartyroomData getPartyroomById(PartyroomId partyroomId) {
        return partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
    }

    public boolean isAlreadyRegistered(PartyroomData partyroom) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        return partyroom.getDjDataSet().stream().anyMatch(dj -> dj.getUserId().equals(authContext.getUserId()));
    }

    @Transactional(readOnly = true)
    public List<DjWithProfileDto> getDjs(PartyroomData partyroom) {
        List<DjData> queuedDjs = partyroom.getDjDataSet().stream()
                .filter(DjData::isQueued)
                .sorted(Comparator.comparingInt(DjData::getOrderNumber))
                .toList();
        List<UserId> userIds = queuedDjs.stream().map(DjData::getUserId).toList();
        Map<UserId, ProfileSettingDto> profileSettingMap = userProfileService.getUsersProfileSetting(userIds);

        return queuedDjs.stream().map(dj -> {
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
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        return partyroomRepository.getActivePartyroomByUserId(authContext.getUserId());
    }

    public Optional<ActivePartyroomDto> getMyActivePartyroom(UserId userId) {
        return partyroomRepository.getActivePartyroomByUserId(userId);
    }

    public Optional<ActivePartyroomWithCrewDto> getMyActivePartyroomWithCrewId(UserId userId) {
        return partyroomRepository.getMyActivePartyroomWithCrewIdByUserId(userId);
    }

    public void getCrews(PartyroomId partyroomId) {
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        List<UserId> crewUserIds = partyroomData.getCrewDataSet().stream().map(CrewData::getUserId).toList();
        Map<UserId, ProfileSettingDto> profileSettings = userProfileService.getUsersProfileSetting(crewUserIds);
    }

    public QueryPartyroomSummaryResponse getSummaryInfo(PartyroomId partyroomId) {
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        if(partyroom.isPlaybackActivated()) {
            PlaybackData playback = playbackInfoService.getPlaybackById(partyroom.getCurrentPlaybackId());
            CrewData djCrew = partyroom.getCrewByUserId(playback.getUserId()).orElseThrow();
            UserId djUserId = djCrew.getUserId();
            ProfileSettingDto profileSettingDto = userProfileService.getUsersProfileSetting(Collections.singletonList(djUserId)).get(djUserId);
            return QueryPartyroomSummaryResponse.from(partyroom, djCrew, profileSettingDto);
        }else {
            return QueryPartyroomSummaryResponse.from(partyroom, null, null);
        }
    }

    @Transactional(readOnly = true)
    public Optional<CrewData> getCrewByUserId(PartyroomId partyroomId, UserId userId) {
        Optional<PartyroomData> optional = partyroomRepository.findById(partyroomId.getId());
        if(optional.isPresent()) {
            PartyroomData partyroom = optional.get();
            return partyroom.getCrewByUserId(userId);
        }else {
            return Optional.empty();
        }
    }
}
