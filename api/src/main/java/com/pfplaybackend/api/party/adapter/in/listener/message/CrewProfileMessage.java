package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.domain.enums.AvatarCompositionType;

import java.io.Serializable;

public record CrewProfileMessage(
        MessageTopic eventType,
        PartyroomId partyroomId,
        long crewId,
        String nickname,
        AvatarCompositionType avatarCompositionType,
        String avatarBodyUri,
        String avatarFaceUri,
        String avatarIconUri,
        int combinePositionX,
        int combinePositionY,
        double offsetX,
        double offsetY,
        double scale
) implements Serializable, GroupBroadcastMessage {

    public static CrewProfileMessage from(PartyroomId partyroomId, Long crewId, CrewProfilePreCheckMessage message) {
        return new CrewProfileMessage(MessageTopic.CREW_PROFILE, partyroomId, crewId,
                message.nickname(),
                message.avatarCompositionType(),
                message.avatarBodyUri(), message.avatarFaceUri(), message.avatarIconUri(),
                message.combinePositionX(), message.combinePositionY(),
                message.offsetX(), message.offsetY(), message.scale());
    }
}
