package com.pfplaybackend.api.profile.application.event;

import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.interfaces.listener.redis.message.CrewProfilePreCheckMessage;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileEventService {
    private final RedisMessagePublisher messagePublisher;

    public void publishProfileChangedEvent(Member member) {
        Profile profile = member.getProfile();
        CrewProfilePreCheckMessage crewProfilePreCheckMessage = new CrewProfilePreCheckMessage(
                profile.getUserId(),
                profile.getNickname(),
                profile.getAvatarFaceUri().getAvatarFaceUri(),
                profile.getAvatarBodyUri().getAvatarBodyUri(),
                profile.getAvatarIconUri().getAvatarIconUri(),
                profile.getAvatarCompositionType(),
                profile.getCombinePositionX(),
                profile.getCombinePositionY(),
                profile.getOffsetX(),
                profile.getOffsetY(),
                profile.getScale()
        );
        messagePublisher.publish(MessageTopic.CREW_PROFILE_PRE_CHECK, crewProfilePreCheckMessage);
    }
}
