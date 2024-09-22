package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomDto;
import com.pfplaybackend.api.partyroom.application.dto.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.partyroom.application.dto.CrewSummaryDto;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.enums.AccessType;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.partyroom.event.message.CrewProfileMessage;
import com.pfplaybackend.api.partyroom.event.message.CrewProfilePreCheckMessage;
import com.pfplaybackend.api.partyroom.event.message.PartyroomAccessMessage;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 해당 서비스는 User 도메인에서의 프로필(닉네임, 아바타 등) 설정 변경 시 호출되는 서비스
 *
 */
@Service
@RequiredArgsConstructor
public class CrewProfileService {

    private final RedisMessagePublisher redisMessagePublisher;
    private final PartyroomInfoService partyroomInfoService;

    /**
     * UserProfileService 로부터 아바타가 변경되었을 경우,
     * 같은 파티 멤버들에게 해당 변경 상태를 통지한다.
     */
    private void publishProfileChangedEvent(ActivePartyroomWithCrewDto dto, CrewProfilePreCheckMessage message) {
        redisMessagePublisher.publish(MessageTopic.CREW_PROFILE,
                CrewProfileMessage.from(new PartyroomId(dto.getId()), dto.getCrewId(), message));
    }

    public void preCheck(CrewProfilePreCheckMessage message) {
        Optional<ActivePartyroomWithCrewDto> optional = partyroomInfoService.getMyActivePartyroomWithCrewId(message.getUserId());
        optional.ifPresent(activePartyroomWithCrewDto -> publishProfileChangedEvent(activePartyroomWithCrewDto, message));
    }
}