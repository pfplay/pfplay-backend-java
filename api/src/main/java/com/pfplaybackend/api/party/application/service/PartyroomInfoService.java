package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.crew.CrewDto;
import com.pfplaybackend.api.party.application.dto.result.CrewProfileSummaryResult;
import com.pfplaybackend.api.party.application.dto.crew.CrewSetupDto;
import com.pfplaybackend.api.party.application.dto.dj.DjWithProfileDto;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.party.adapter.in.web.payload.response.info.QueryPartyroomSummaryResponse;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyroomInfoService {

    private final PartyroomRepository partyroomRepository;
    private final CrewRepository crewRepository;
    private final DjRepository djRepository;
    private final UserProfileQueryPort userProfileQueryPort;
    private final PlaybackInfoService playbackInfoService;

    @Transactional(readOnly = true)
    public List<PartyroomWithCrewDto> getAllPartyrooms() {
        return partyroomRepository.getCrewDataByPartyroomId().stream().map(partyroomWithCrewDto -> {
            List<CrewDto> filteredCrews = partyroomWithCrewDto.getCrews().stream()
                                .filter(crewDto -> crewDto.getGradeType().isEqualOrHigherThan(GradeType.MODERATOR))
                                .limit(3)
                                .toList();
            return PartyroomWithCrewDto.from(partyroomWithCrewDto, filteredCrews);
        }).toList();
    }

    @Transactional(readOnly = true)
    public Map<UserId, ProfileSettingDto> getPrimariesAvatarSettings(List<PartyroomWithCrewDto> partyrooms) {
        List<UserId> primaryUserIds = partyrooms.stream().collect(Collectors.toMap(
                        PartyroomWithCrewDto::getPartyroomId,
                        partyroomWithCrewDto -> partyroomWithCrewDto.getCrews().stream().toList()
                )).values().stream()
                .flatMap(List::stream)
                .map(CrewDto::getUserId).toList();
        return userProfileQueryPort.getUsersProfileSetting(primaryUserIds);
    }

    // 초기화를 위한 파티멤버 목록 조회
    @Transactional(readOnly = true)
    public List<CrewSetupDto> getCrewsForSetup(PartyroomId partyroomId) {
        List<CrewData> crews = crewRepository.findByPartyroomDataIdAndIsActiveTrue(partyroomId.getId());
        List<UserId> userIds = crews.stream().map(CrewData::getUserId).toList();
        Map<UserId, ProfileSettingDto> profileSettingMap = userProfileQueryPort.getUsersProfileSetting(userIds);

        return crews.stream().map(crew -> {
            UserId userId = crew.getUserId();
            return CrewSetupDto.from(crew, profileSettingMap.get(userId));
        }).toList();
    }

    @Transactional(readOnly = true)
    public PartyroomData getPartyroomById(PartyroomId partyroomId) {
        return partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
    }

    @Transactional(readOnly = true)
    public boolean isAlreadyRegistered(Long partyroomId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        return djRepository.existsByPartyroomDataIdAndUserIdAndIsQueuedTrue(partyroomId, authContext.getUserId());
    }

    @Transactional(readOnly = true)
    public List<DjWithProfileDto> getDjs(Long partyroomId) {
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdAndIsQueuedTrueOrderByOrderNumberAsc(partyroomId);
        List<UserId> userIds = queuedDjs.stream().map(DjData::getUserId).toList();
        Map<UserId, ProfileSettingDto> profileSettingMap = userProfileQueryPort.getUsersProfileSetting(userIds);

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

    @Transactional(readOnly = true)
    public Optional<ActivePartyroomDto> getMyActivePartyroom() {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        return partyroomRepository.getActivePartyroomByUserId(authContext.getUserId());
    }

    @Transactional(readOnly = true)
    public Optional<ActivePartyroomDto> getMyActivePartyroom(UserId userId) {
        return partyroomRepository.getActivePartyroomByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<ActivePartyroomWithCrewDto> getMyActivePartyroomWithCrewId(UserId userId) {
        return partyroomRepository.getMyActivePartyroomWithCrewIdByUserId(userId);
    }

    @Transactional(readOnly = true)
    public QueryPartyroomSummaryResponse getSummaryInfo(PartyroomId partyroomId) {
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        if(partyroom.isPlaybackActivated()) {
            PlaybackData playback = playbackInfoService.getPlaybackById(partyroom.getCurrentPlaybackId());
            CrewData djCrew = crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), playback.getUserId())
                    .orElseThrow();
            UserId djUserId = djCrew.getUserId();
            ProfileSettingDto profileSettingDto = userProfileQueryPort.getUsersProfileSetting(Collections.singletonList(djUserId)).get(djUserId);
            return QueryPartyroomSummaryResponse.from(partyroom, djCrew, profileSettingDto);
        }else {
            return QueryPartyroomSummaryResponse.from(partyroom, null, null);
        }
    }

    @Transactional(readOnly = true)
    public Optional<CrewData> getCrewByUserId(PartyroomId partyroomId, UserId userId) {
        return crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), userId);
    }

    @Transactional(readOnly = true)
    public CrewProfileSummaryResult getProfileSummaryByCrewId(Long crewId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        ActivePartyroomWithCrewDto activePartyroomDto = getMyActivePartyroomWithCrewId(authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));

        CrewData crew = crewRepository.findById(crewId)
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));
        UserId targetUserId = crew.getUserId();
        AuthorityTier authorityTier = crew.getAuthorityTier();

        ProfileSummaryDto profileSummaryDto = userProfileQueryPort.getOtherProfileSummary(targetUserId, authorityTier);
        return CrewProfileSummaryResult.from(crewId, profileSummaryDto);
    }
}
