package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.application.dto.command.CrewProfileChangedCommand;
import com.pfplaybackend.api.party.application.dto.shared.AvatarProfile;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;
import java.util.UUID;

public record CrewProfileMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        String id,
        long timestamp,
        long crewId,
        String nickname,
        AvatarProfile avatar
) implements Serializable, GroupBroadcastMessage {

    public static CrewProfileMessage from(CrewProfileChangedCommand command) {
        return new CrewProfileMessage(
                command.partyroomId(),
                MessageTopic.CREW_PROFILE_CHANGED,
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                command.crewId(),
                command.nickname(),
                AvatarProfile.from(command.avatarCompositionType(),
                        command.avatarBodyUri(), command.avatarFaceUri(), command.avatarIconUri(),
                        command.combinePositionX(), command.combinePositionY(),
                        command.offsetX(), command.offsetY(), command.scale())
        );
    }
}
