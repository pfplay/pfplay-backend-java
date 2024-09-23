package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.domain.value.CrewId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.partyroom.event.message.CrewPenaltyMessage;
import com.pfplaybackend.api.partyroom.presentation.payload.request.regulation.PunishPenaltyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewPenaltyService {

    private final RedisMessagePublisher messagePublisher;

    public void updatePenalty(PartyroomId partyroomId, CrewId crewId, PunishPenaltyRequest request) {
        // TODO
        CrewPenaltyMessage message = new CrewPenaltyMessage();
        publishCrewPenaltyChangedEvent(message);
    }

    // CHAT_BAN_30_SECONDS("채팅 금지", 30), // 채팅 금지 이벤트
    // 채팅 금지 페널티는 '실제 채팅 내용 자체'를 아예 전파하지도 않아야 한다.
    // CHAT_MESSAGE_REMOVAL("채팅 메시지 삭제", 0), // 채팅 메시지 삭제 이벤트
    // EXPULSION_ONE_TIME("일회성 강제 퇴장", 0), // 지속 시간이 필요 없으므로 0으로 설정
    // EXPULSION_PERMANENT("영구 강제 퇴장", -1); // 영구 강제 퇴장을 -1로 설정

    // 명시적으로 해제 가능한 페널티는 '30초간 채팅 금지'와 '영구 강제 퇴장'이다.

    private void publishCrewPenaltyChangedEvent(CrewPenaltyMessage message) {
        messagePublisher.publish(MessageTopic.CREW_PENALTY, message);
    }
}
