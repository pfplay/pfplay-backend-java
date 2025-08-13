package com.pfplaybackend.api.party.interfaces.listener.redis.message;

import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CrewProfileMessage implements Serializable {
    private MessageTopic eventType;
    private PartyroomId partyroomId;
    private long crewId;
    private String nickname;
    private String avatarFaceUri;
    private String avatarBodyUri;
    private String avatarIconUri;
    private AvatarCompositionType avatarCompositionType;
    private int combinePositionX;
    private int combinePositionY;
    private double offsetX;
    private double offsetY;
    private double scale;

    public static CrewProfileMessage from(PartyroomId partyroomId , Long crewId, CrewProfilePreCheckMessage message) {
        return new CrewProfileMessage(MessageTopic.CREW_PROFILE, partyroomId, crewId,
                message.getNickname(), message.getAvatarFaceUri(), message.getAvatarBodyUri(), message.getAvatarIconUri(),
                message.getAvatarCompositionType(),
                message.getCombinePositionX(), message.getCombinePositionY(),
                message.getOffsetX(), message.getOffsetY(), message.getScale());
    }
}
