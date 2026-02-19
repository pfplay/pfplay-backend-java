package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.result.PenaltyResult;
import com.pfplaybackend.api.party.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewPenaltyHistoryData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import com.pfplaybackend.api.party.domain.exception.PenaltyException;
import com.pfplaybackend.api.party.domain.service.CrewDomainService;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.infrastructure.repository.CrewPenaltyHistoryRepository;
import com.pfplaybackend.api.party.infrastructure.repository.CrewRepository;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.CrewPenaltyMessage;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.regulation.PunishPenaltyRequest;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CrewPenaltyService {

    private final RedisMessagePublisher messagePublisher;
    private final PartyroomRepository partyroomRepository;
    private final CrewRepository crewRepository;
    private final CrewDomainService crewDomainService;
    private final PartyroomAccessService partyroomAccessService;
    private final CrewPenaltyHistoryRepository crewPenaltyHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserProfilePeerService userProfileService;

    public List<PenaltyResult> getPenalties(PartyroomId partyroomId) {
        List<CrewPenaltyHistoryData> crewPenaltyHistoryDataList = crewPenaltyHistoryRepository.findAllByPartyroomIdAndReleasedIsFalse(partyroomId);

        List<UserId> PunishedUserIds = crewPenaltyHistoryDataList.stream().map(history -> {
            CrewData crew = crewRepository.findById(history.getPunishedCrewId().getId())
                    .orElseThrow();
            return crew.getUserId();
        }).toList();

        Map<UserId, ProfileSettingDto> map = userProfileService.getUsersProfileSetting(PunishedUserIds);

        return crewPenaltyHistoryDataList.stream().map(history -> {
            CrewData crew = crewRepository.findById(history.getPunishedCrewId().getId())
                    .orElseThrow();
            UserId userId = crew.getUserId();
            ProfileSettingDto profileSettingDto = map.get(userId);
            return PenaltyResult.from(history, profileSettingDto);
        }).toList();
    }

    @Transactional
    public void addPenalty(PartyroomId partyroomId, PunishPenaltyRequest request) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

        CrewId punishedCrewId = new CrewId(request.getCrewId());
        CrewData punisherCrew = crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), authContext.getUserId())
                .orElseThrow();
        CrewData punishedCrew = crewRepository.findById(punishedCrewId.getId())
                .orElseThrow();
        GradeType punishedGradeType = punishedCrew.getGradeType();
        PenaltyType penaltyType = request.getPenaltyType();

        // 권한 검증
        if(crewDomainService.isBelowManagerGrade(partyroomId.getId(), authContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        if(crewDomainService.isAdjusterGradeLowerThanSubject(partyroomId.getId(), authContext.getUserId(), punishedCrewId)) throw ExceptionCreator.create(GradeException.GRADE_INSUFFICIENT_FOR_OPERATION);
        if(crewDomainService.isTargetGradeExceedingAdjuster(partyroomId.getId(), authContext.getUserId(), punishedGradeType)) throw ExceptionCreator.create(GradeException.GRADE_EXCEEDS_ALLOWED_THRESHOLD);

        // 페널티 부과
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        if(penaltyType.equals(PenaltyType.CHAT_BAN_30_SECONDS)) recordInShortTime(punishedCrewId.getId());
        if(penaltyType.equals(PenaltyType.ONE_TIME_EXPULSION)) partyroomAccessService.expel(partyroom, punishedCrew, false);
        if(penaltyType.equals(PenaltyType.PERMANENT_EXPULSION)) partyroomAccessService.expel(partyroom, punishedCrew, true);

        CrewPenaltyMessage message = CrewPenaltyMessage.from(partyroomId, new CrewId(punisherCrew.getId()), punishedCrewId, request.getDetail(), request.getPenaltyType());
        publishCrewPenaltyChangedEvent(message);

        if(PenaltyType.PERMANENT_EXPULSION.equals(penaltyType)) {
            CrewPenaltyHistoryData crewPenaltyHistoryData = CrewPenaltyHistoryData.builder()
                    .partyroomId(partyroomId)
                    .punishedCrewId(punishedCrewId)
                    .punisherCrewId(new CrewId(punisherCrew.getId()))
                    .penaltyReason(request.getDetail())
                    .penaltyDate(LocalDateTime.now())
                    .penaltyType(request.getPenaltyType())
                    .released(false)
                    .build();
            crewPenaltyHistoryRepository.save(crewPenaltyHistoryData);
        }
    }

    private void recordInShortTime(Long crewIdValue) {
        String key = "PENALTY:CHAT_BAN:" + crewIdValue;
        redisTemplate.opsForValue().set(key, 30, Duration.ofSeconds(30));
    }

    public void releaseCrewPenalty(PartyroomId partyroomId, Long penaltyId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

        if(crewDomainService.isBelowManagerGrade(partyroomId.getId(), authContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);

        CrewPenaltyHistoryData historyData = crewPenaltyHistoryRepository.findByIdAndPartyroomIdAndReleasedIsFalse(penaltyId, partyroomId)
                .orElseThrow(() -> ExceptionCreator.create(PenaltyException.PENALTY_HISTORY_NOT_FOUND));

        // 1. Release ban on crew
        CrewData crew = crewRepository.findById(historyData.getPunishedCrewId().getId())
                .orElseThrow();
        crew.releaseBan();
        crewRepository.save(crew);

        // 2. Update history
        CrewData releaserCrew = crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), authContext.getUserId())
                .orElseThrow();
        historyData.setReleased(true);
        historyData.setReleasedByCrewId(new CrewId(releaserCrew.getId()));
        historyData.setReleaseDate(LocalDateTime.now());
        crewPenaltyHistoryRepository.save(historyData);
    }

    private void publishCrewPenaltyChangedEvent(CrewPenaltyMessage message) {
        messagePublisher.publish(MessageTopic.CREW_PENALTY, message);
    }
}
