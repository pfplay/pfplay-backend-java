package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.application.aspect.context.PartyContext;
import com.pfplaybackend.api.partyroom.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.partyroom.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.service.CrewDomainService;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.partyroom.event.message.CrewPenaltyMessage;
import com.pfplaybackend.api.partyroom.exception.GradeException;
import com.pfplaybackend.api.partyroom.exception.PartyroomException;
import com.pfplaybackend.api.partyroom.presentation.payload.request.regulation.PunishPenaltyRequest;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrewPenaltyService {

    private final RedisMessagePublisher messagePublisher;
    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final CrewDomainService crewDomainService;

    public void addPenalty(PartyroomId partyroomId, CrewId punishedCrewId, PunishPenaltyRequest request) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(partyroomId);
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomDataDto partyroomDataDto = optional.get();
        PartyroomData partyroomData = partyroomConverter.toEntity(partyroomDataDto);
        // TODO 1. 파티룸 조회
        // TODO 2. 같은 파티룸에 위치하는가?

        // 권한 검증
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        Crew punisherCrew = partyroom.getCrewByUserId(partyContext.getUserId()).orElseThrow();
        GradeType punishedGradeType = partyroom.getCrew(punishedCrewId).getGradeType();
        if(crewDomainService.isBelowManagerGrade(partyroom, partyContext.getUserId())) throw ExceptionCreator.create(GradeException.MANAGER_GRADE_REQUIRED);
        if(crewDomainService.isAdjusterGradeLowerThanSubject(partyroom, partyContext.getUserId(), punishedCrewId)) throw ExceptionCreator.create(GradeException.GRADE_INSUFFICIENT_FOR_OPERATION);
        if(crewDomainService.isTargetGradeExceedingAdjuster(partyroom, partyContext.getUserId(), punishedGradeType)) throw ExceptionCreator.create(GradeException.GRADE_EXCEEDS_ALLOWED_THRESHOLD);
        // TODO 4. 채팅 금지 → save in Redis key
        // TODO 5. 임시 퇴장 → exit()
        // TODO 6. 강제 퇴장 → exit(), isBanned()
        // TODO 7. 페널티 부과 이벤트 발행
        // TODO 8. 페널티 부과 이력 기록
        CrewPenaltyMessage message = CrewPenaltyMessage.from(partyroomId, new CrewId(punisherCrew.getId()), punishedCrewId, request.getReason(), request.getPenaltyType());
        publishCrewPenaltyChangedEvent(message);
    }

    // CHAT_BAN_30_SECONDS("채팅 금지", 30), // 채팅 금지 이벤트
    // 채팅 금지 페널티는 '실제 채팅 내용 자체'를 아예 전파하지도 않아야 한다.
    // CHAT_MESSAGE_REMOVAL("채팅 메시지 삭제", 0), // 채팅 메시지 삭제 이벤트
    // EXPULSION_ONE_TIME("일회성 강제 퇴장", 0), // 지속 시간이 필요 없으므로 0으로 설정
    // EXPULSION_PERMANENT("영구 강제 퇴장", -1); // 영구 강제 퇴장을 -1로 설정

    private void publishCrewPenaltyChangedEvent(CrewPenaltyMessage message) {
        messagePublisher.publish(MessageTopic.CREW_PENALTY, message);
    }
}
