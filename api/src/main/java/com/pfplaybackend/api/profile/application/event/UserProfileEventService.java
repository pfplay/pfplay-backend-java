package com.pfplaybackend.api.profile.application.event;

import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.adapter.in.listener.message.CrewProfilePreCheckMessage;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileEventService {
    private final RedisMessagePublisher messagePublisher;

    public void publishProfileChangedEvent(MemberData member) {
        ProfileData profile = member.getProfileData();
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
        messagePublisher.publish(MessageTopic.CREW_PROFILE_PRE_CHECK.topic(), crewProfilePreCheckMessage);
    }
}
