package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.application.dto.command.CrewProfileChangedCommand;
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

    public static CrewProfileMessage from(CrewProfileChangedCommand command) {
        return new CrewProfileMessage(MessageTopic.CREW_PROFILE, command.partyroomId(), command.crewId(),
                command.nickname(),
                command.avatarCompositionType(),
                command.avatarBodyUri(), command.avatarFaceUri(), command.avatarIconUri(),
                command.combinePositionX(), command.combinePositionY(),
                command.offsetX(), command.offsetY(), command.scale());
    }
}
