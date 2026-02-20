package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.result.PenaltyResult;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewPenaltyHistoryData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import com.pfplaybackend.api.party.domain.exception.PenaltyException;
import com.pfplaybackend.api.party.domain.event.CrewPenalizedEvent;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewPenaltyHistoryRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.regulation.PunishPenaltyRequest;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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

    private final ApplicationEventPublisher eventPublisher;
    private final PartyroomRepository partyroomRepository;
    private final CrewRepository crewRepository;
    private final PartyroomAccessService partyroomAccessService;
    private final CrewPenaltyHistoryRepository crewPenaltyHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserProfileQueryPort userProfileQueryPort;

    public List<PenaltyResult> getPenalties(PartyroomId partyroomId) {
        List<CrewPenaltyHistoryData> crewPenaltyHistoryDataList = crewPenaltyHistoryRepository.findAllByPartyroomIdAndReleasedIsFalse(partyroomId);

        List<UserId> PunishedUserIds = crewPenaltyHistoryDataList.stream().map(history -> {
            CrewData crew = crewRepository.findById(history.getPunishedCrewId().getId())
                    .orElseThrow();
            return crew.getUserId();
        }).toList();

        Map<UserId, ProfileSettingDto> map = userProfileQueryPort.getUsersProfileSetting(PunishedUserIds);

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
        if (punisherCrew.isBelowGrade(GradeType.MODERATOR)) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        if (punisherCrew.getGradeType().isLowerThan(punishedCrew.getGradeType())) throw ExceptionCreator.create(GradeException.GRADE_INSUFFICIENT_FOR_OPERATION);
        if (punishedGradeType.isEqualOrHigherThan(punisherCrew.getGradeType())) throw ExceptionCreator.create(GradeException.GRADE_EXCEEDS_ALLOWED_THRESHOLD);

        // 페널티 부과
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        if(penaltyType.equals(PenaltyType.CHAT_BAN_30_SECONDS)) recordInShortTime(punishedCrewId.getId());
        if(penaltyType.equals(PenaltyType.ONE_TIME_EXPULSION)) partyroomAccessService.expel(partyroom, punishedCrew, false);
        if(penaltyType.equals(PenaltyType.PERMANENT_EXPULSION)) partyroomAccessService.expel(partyroom, punishedCrew, true);

        eventPublisher.publishEvent(new CrewPenalizedEvent(
                partyroomId, new CrewId(punisherCrew.getId()), punishedCrewId, request.getDetail(), request.getPenaltyType()));

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

        CrewData releaserCrewForValidation = crewRepository.findByPartyroomDataIdAndUserId(partyroomId.getId(), authContext.getUserId())
                .orElseThrow();
        if (releaserCrewForValidation.isBelowGrade(GradeType.MODERATOR)) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);

        CrewPenaltyHistoryData historyData = crewPenaltyHistoryRepository.findByIdAndPartyroomIdAndReleasedIsFalse(penaltyId, partyroomId)
                .orElseThrow(() -> ExceptionCreator.create(PenaltyException.PENALTY_HISTORY_NOT_FOUND));

        // 1. Release ban on crew
        CrewData crew = crewRepository.findById(historyData.getPunishedCrewId().getId())
                .orElseThrow();
        crew.releaseBan();
        crewRepository.save(crew);

        // 2. Update history
        historyData.setReleased(true);
        historyData.setReleasedByCrewId(new CrewId(releaserCrewForValidation.getId()));
        historyData.setReleaseDate(LocalDateTime.now());
        crewPenaltyHistoryRepository.save(historyData);
    }

}
