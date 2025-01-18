package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
import com.pfplaybackend.api.party.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.party.application.dto.result.PenaltyResult;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewPenaltyHistoryData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import com.pfplaybackend.api.party.domain.exception.PenaltyException;
import com.pfplaybackend.api.party.domain.service.CrewDomainService;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.infrastructure.repository.CrewPenaltyHistoryRepository;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.CrewPenaltyMessage;
import com.pfplaybackend.api.party.domain.exception.GradeException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.regulation.PunishPenaltyRequest;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrewPenaltyService {

    private final RedisMessagePublisher messagePublisher;
    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final CrewDomainService crewDomainService;
    private final PartyroomAccessService partyroomAccessService;
    private final CrewPenaltyHistoryRepository crewPenaltyHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<PenaltyResult> getPenalties(PartyroomId partyroomId) {
        List<CrewPenaltyHistoryData> crewPenaltyHistoryDataList = crewPenaltyHistoryRepository.findAllByPartyroomIdAndReleasedIsFalse(partyroomId);
        return crewPenaltyHistoryDataList.stream().map(PenaltyResult::from).toList();
    }

    // TODO '권한 검증' 전용 어노테이션 부착 필요
    @Transactional
    public void addPenalty(PartyroomId partyroomId, PunishPenaltyRequest request) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomDataDto partyroomDataDto = optional.get();
        PartyroomData partyroomData = partyroomConverter.toEntity(partyroomDataDto);
        // FIXME 1. 파티룸 조회
        // partyroomRepository.findPartyroomDto(PartyroomId partyroomId, UserId userId);
        // TODO 2. 같은 파티룸에 위치하는가?
        // crewDomainService.isExistIn();

        CrewId punishedCrewId = new CrewId(request.getCrewId());
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        Crew punisherCrew = partyroom.getCrewByUserId(partyContext.getUserId()).orElseThrow();
        Crew punishedCrew = partyroom.getCrew(punishedCrewId);
        GradeType punishedGradeType = partyroom.getCrew(punishedCrewId).getGradeType();
        PenaltyType penaltyType = request.getPenaltyType();

        // 권한 검증
        if(crewDomainService.isBelowManagerGrade(partyroom, partyContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        if(crewDomainService.isAdjusterGradeLowerThanSubject(partyroom, partyContext.getUserId(), punishedCrewId)) throw ExceptionCreator.create(GradeException.GRADE_INSUFFICIENT_FOR_OPERATION);
        if(crewDomainService.isTargetGradeExceedingAdjuster(partyroom, partyContext.getUserId(), punishedGradeType)) throw ExceptionCreator.create(GradeException.GRADE_EXCEEDS_ALLOWED_THRESHOLD);

        // 페널티 부과
        if(penaltyType.equals(PenaltyType.CHAT_BAN_30_SECONDS)) recordInShortTime(punishedCrewId.getId());
        if(penaltyType.equals(PenaltyType.ONE_TIME_EXPULSION)) partyroomAccessService.expel(partyroom, punishedCrew, false);
        if(penaltyType.equals(PenaltyType.PERMANENT_EXPULSION)) partyroomAccessService.expel(partyroom, punishedCrew, true);

        CrewPenaltyMessage message = CrewPenaltyMessage.from(partyroomId, new CrewId(punisherCrew.getId()), punishedCrewId, request.getDetail(), request.getPenaltyType());
        publishCrewPenaltyChangedEvent(message);

        // TODO 타겟의 페널티 중복 부과 여부 검증 필요
        if(PenaltyType.PERMANENT_EXPULSION.equals(penaltyType)) {
            CrewPenaltyHistoryData crewPenaltyHistoryData = CrewPenaltyHistoryData.builder()
                    .partyroomId(partyroomId)
                    .punishedCrewId(punishedCrewId)
                    .punisherCrewId(new CrewId(punisherCrew.getId()))
                    .penaltyReason(request.getDetail())
                    .penaltyDate(LocalDateTime.now())
                    .penaltyType(request.getPenaltyType())
                    .build();
            crewPenaltyHistoryRepository.save(crewPenaltyHistoryData);
        }
    }

    private void recordInShortTime(Long crewIdValue) {
        String key = "PENALTY:CHAT_BAN:" + crewIdValue;
        redisTemplate.opsForValue().set(key, 30, Duration.ofSeconds(30));
    }

    // CHAT_BAN_30_SECONDS("채팅 금지", 30), // 채팅 금지 이벤트
    // 채팅 금지 페널티는 '실제 채팅 내용 자체'를 아예 전파하지도 않아야 한다.
    // CHAT_MESSAGE_REMOVAL("채팅 메시지 삭제", 0), // 채팅 메시지 삭제 이벤트
    // EXPULSION_ONE_TIME("일회성 강제 퇴장", 0), // 지속 시간이 필요 없으므로 0으로 설정
    // EXPULSION_PERMANENT("영구 강제 퇴장", -1); // 영구 강제 퇴장을 -1로 설정

    public void releaseCrewPenalty(PartyroomId partyroomId, Long penaltyId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        // TODO 호출 크루가 타겟 크루와 동일한 파티룸에 위치해야 하며, 권한 검증도 필요하다.
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);

        // TODO 업무 규칙 확인 필요
        if(crewDomainService.isBelowManagerGrade(partyroom, partyContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);

        CrewPenaltyHistoryData historyData = crewPenaltyHistoryRepository.findByIdAndPartyroomIdAndReleasedIsFalse(penaltyId, partyroomId)
                .orElseThrow(() -> ExceptionCreator.create(PenaltyException.PENALTY_HISTORY_NOT_FOUND));

        // 1.
        partyroom.removePermanentBan(historyData.getPunishedCrewId());
        partyroomRepository.save(partyroomConverter.toData(partyroom));
        // 2.
        historyData.setReleased(true);
        historyData.setReleasedByCrewId(new CrewId(partyroom.getCrewByUserId(partyContext.getUserId()).orElseThrow().getId()));
        historyData.setReleaseDate(LocalDateTime.now());
        crewPenaltyHistoryRepository.save(historyData);
    }

    private void publishCrewPenaltyChangedEvent(CrewPenaltyMessage message) {
        messagePublisher.publish(MessageTopic.CREW_PENALTY, message);
    }
}
