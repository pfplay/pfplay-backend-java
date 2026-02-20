package com.pfplaybackend.api.user.application.event;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.user.application.dto.ProfileChangedEvent;
import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileEventService {
    private final RedisMessagePublisher messagePublisher;

    public void publishProfileChangedEvent(MemberData member) {
        ProfileData profile = member.getProfileData();
        var avatar = profile.getAvatarSetting();
        ProfileChangedEvent event = new ProfileChangedEvent(
                profile.getUserId(),
                profile.getNicknameValue(),
                avatar.getAvatarFaceUri().getAvatarFaceUri(),
                avatar.getAvatarBodyUri().getAvatarBodyUri(),
                avatar.getAvatarIconUri().getAvatarIconUri(),
                avatar.getAvatarCompositionType(),
                avatar.getCombinePositionX(),
                avatar.getCombinePositionY(),
                avatar.getOffsetX(),
                avatar.getOffsetY(),
                avatar.getScale()
        );
        messagePublisher.publish(MessageTopic.CREW_PROFILE_PRE_CHECK.topic(), event);
    }
}
