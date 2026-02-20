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
import com.pfplaybackend.api.party.adapter.in.web.payload.request.regulation.PunishPenaltyRequest;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrewPenaltyService {

    private final ApplicationEventPublisher eventPublisher;
    private final CrewRepository crewRepository;
    private final PartyroomAccessService partyroomAccessService;
    private final CrewPenaltyHistoryRepository crewPenaltyHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserProfileQueryPort userProfileQueryPort;
    private final PartyroomInfoService partyroomInfoService;

    public List<PenaltyResult> getPenalties(PartyroomId partyroomId) {
        List<CrewPenaltyHistoryData> crewPenaltyHistoryDataList = crewPenaltyHistoryRepository.findAllByPartyroomIdAndReleasedIsFalse(partyroomId);

        List<Long> crewIds = crewPenaltyHistoryDataList.stream()
                .map(history -> history.getPunishedCrewId().getId())
                .distinct()
                .toList();
        Map<Long, CrewData> crewMap = crewRepository.findAllById(crewIds).stream()
                .collect(Collectors.toMap(CrewData::getId, Function.identity()));

        List<UserId> punishedUserIds = crewPenaltyHistoryDataList.stream()
                .map(history -> crewMap.get(history.getPunishedCrewId().getId()).getUserId())
                .toList();
        Map<UserId, ProfileSettingDto> profileMap = userProfileQueryPort.getUsersProfileSetting(punishedUserIds);

        return crewPenaltyHistoryDataList.stream().map(history -> {
            CrewData crew = crewMap.get(history.getPunishedCrewId().getId());
            return PenaltyResult.from(history, profileMap.get(crew.getUserId()));
        }).toList();
    }

    @Transactional
    public void addPenalty(PartyroomId partyroomId, PunishPenaltyRequest request) {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        PartyroomData partyroom = partyroomInfoService.getPartyroomById(partyroomId);

        CrewId punishedCrewId = new CrewId(request.getCrewId());
        CrewData punisherCrew = partyroomInfoService.getCrewOrThrow(partyroomId.getId(), authContext.getUserId());
        CrewData punishedCrew = crewRepository.findById(punishedCrewId.getId())
                .orElseThrow();
        GradeType punishedGradeType = punishedCrew.getGradeType();
        PenaltyType penaltyType = request.getPenaltyType();

        // 권한 검증
        if (punisherCrew.isBelowGrade(GradeType.MODERATOR)) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        if (punisherCrew.getGradeType().isLowerThan(punishedCrew.getGradeType())) throw ExceptionCreator.create(GradeException.GRADE_INSUFFICIENT_FOR_OPERATION);
        if (punishedGradeType.isEqualOrHigherThan(punisherCrew.getGradeType())) throw ExceptionCreator.create(GradeException.GRADE_EXCEEDS_ALLOWED_THRESHOLD);

        // 페널티 부과
        switch (penaltyType) {
            case CHAT_MESSAGE_REMOVAL -> {} // 이벤트로만 처리
            case CHAT_BAN_30_SECONDS -> recordInShortTime(punishedCrewId.getId());
            case ONE_TIME_EXPULSION -> partyroomAccessService.expel(partyroom, punishedCrew, false);
            case PERMANENT_EXPULSION -> partyroomAccessService.expel(partyroom, punishedCrew, true);
        }

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
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        partyroomInfoService.getPartyroomById(partyroomId);

        CrewData releaserCrewForValidation = partyroomInfoService.getCrewOrThrow(partyroomId.getId(), authContext.getUserId());
        if (releaserCrewForValidation.isBelowGrade(GradeType.MODERATOR)) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);

        CrewPenaltyHistoryData historyData = crewPenaltyHistoryRepository.findByIdAndPartyroomIdAndReleasedIsFalse(penaltyId, partyroomId)
                .orElseThrow(() -> ExceptionCreator.create(PenaltyException.PENALTY_HISTORY_NOT_FOUND));

        // 1. Release ban on crew
        CrewData crew = crewRepository.findById(historyData.getPunishedCrewId().getId())
                .orElseThrow();
        crew.releaseBan();
        crewRepository.save(crew);

        // 2. Update history
        historyData.release(new CrewId(releaserCrewForValidation.getId()));
        crewPenaltyHistoryRepository.save(historyData);
    }

}
