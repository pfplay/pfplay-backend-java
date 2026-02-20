package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.adapter.in.listener.message.CrewProfileMessage;
import com.pfplaybackend.api.party.adapter.in.listener.message.CrewProfilePreCheckMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * User 도메인에서의 프로필(닉네임, 아바타 등) 설정 변경 시 호출되는 핸들러.
 * 같은 파티 멤버들에게 해당 변경 상태를 통지한다.
 */
@Service
@RequiredArgsConstructor
public class CrewProfileChangeHandler {

    private final RedisMessagePublisher messagePublisher;
    private final PartyroomInfoService partyroomInfoService;

    private void publishProfileChangedEvent(ActivePartyroomWithCrewDto dto, CrewProfilePreCheckMessage message) {
        messagePublisher.publish(MessageTopic.CREW_PROFILE.topic(),
                CrewProfileMessage.from(new PartyroomId(dto.getId()), dto.getCrewId(), message));
    }

    public void preCheck(CrewProfilePreCheckMessage message) {
        Optional<ActivePartyroomWithCrewDto> optional = partyroomInfoService.getMyActivePartyroomWithCrewId(message.getUserId());
        optional.ifPresent(activePartyroomWithCrewDto -> publishProfileChangedEvent(activePartyroomWithCrewDto, message));
    }
}
