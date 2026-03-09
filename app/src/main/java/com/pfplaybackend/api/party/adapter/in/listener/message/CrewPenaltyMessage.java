package com.pfplaybackend.api.party.adapter.in.listener.message;

import com.pfplaybackend.api.common.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.enums.PenaltyType;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;

import java.io.Serializable;
import java.util.UUID;

public record CrewPenaltyMessage(
        PartyroomId partyroomId,
        MessageTopic eventType,
        String id,
        long timestamp,
        PenaltyType penaltyType,
        String detail,
        PunisherInfo punisher,
        PunishedInfo punished
) implements Serializable, GroupBroadcastMessage {

    public record PunisherInfo(long crewId) {}
    public record PunishedInfo(long crewId) {}

    public static CrewPenaltyMessage from(PartyroomId partyroomId,
                                        CrewId punisherCrewId, CrewId punishedCrewId, String detail, PenaltyType penaltyType) {
        return new CrewPenaltyMessage(
                partyroomId,
                MessageTopic.CREW_PENALIZED,
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                penaltyType,
                detail,
                new PunisherInfo(punisherCrewId.getId()),
                new PunishedInfo(punishedCrewId.getId())
        );
    }
}
