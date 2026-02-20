package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;
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
    private AvatarCompositionType avatarCompositionType;
    private String avatarBodyUri;
    private String avatarFaceUri;
    private String avatarIconUri;
    private int combinePositionX;
    private int combinePositionY;
    private double offsetX;
    private double offsetY;
    private double scale;

    public static CrewProfileMessage from(PartyroomId partyroomId , Long crewId, CrewProfilePreCheckMessage message) {
        return new CrewProfileMessage(MessageTopic.CREW_PROFILE, partyroomId, crewId,
                message.getNickname(),
                message.getAvatarCompositionType(),
                message.getAvatarBodyUri(), message.getAvatarFaceUri(), message.getAvatarIconUri(),
                message.getCombinePositionX(), message.getCombinePositionY(),
                message.getOffsetX(), message.getOffsetY(), message.getScale());
    }
}
