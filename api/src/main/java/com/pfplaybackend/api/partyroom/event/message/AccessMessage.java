package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.application.dto.PartymemberSummaryDto;
import com.pfplaybackend.api.partyroom.domain.enums.AccessType;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccessMessage implements Serializable {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private AccessType accessType;
    private PartymemberSummaryDto member;

    public static AccessMessage create(PartyroomId partyroomId, AccessType accessType, PartymemberSummaryDto partymemberSummaryDto) {
        return new AccessMessage(
                partyroomId,
                MessageTopic.ACCESS,
                accessType,
                partymemberSummaryDto);
    }
}