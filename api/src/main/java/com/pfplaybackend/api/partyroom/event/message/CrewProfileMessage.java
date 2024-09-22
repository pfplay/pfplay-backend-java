package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.event.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
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
    private int combinePositionX;
    private int combinePositionY;

    public static CrewProfileMessage from(MessageTopic eventType, Crew crew, CrewProfilePreCheckMessage message) {
        return new CrewProfileMessage(eventType, crew.getPartyroomId(), crew.getId(),
                message.getNickname(), message.getAvatarFaceUri(), message.getAvatarBodyUri(), message.getAvatarIconUri(),
                message.getCombinePositionX(), message.getCombinePositionY());
    }
}
