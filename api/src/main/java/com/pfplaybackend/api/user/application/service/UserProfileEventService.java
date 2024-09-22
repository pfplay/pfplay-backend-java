package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.CrewProfilePreCheckMessage;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileEventService {
    private final RedisMessagePublisher redisMessagePublisher;

    public void publishProfileChangedEvent(Member member) {
        Profile profile = member.getProfile();
        CrewProfilePreCheckMessage crewProfilePreCheckMessage = new CrewProfilePreCheckMessage(
                profile.getUserId(),
                profile.getNickname(),
                profile.getAvatarFaceUri().toString(),
                profile.getAvatarBodyUri().toString(),
                profile.getAvatarIconUri().toString(),
                profile.getCombinePositionX(),
                profile.getCombinePositionY()
        );
        redisMessagePublisher.publish(MessageTopic.CREW_PROFILE_PRE_CHECK, crewProfilePreCheckMessage);
    }
}
