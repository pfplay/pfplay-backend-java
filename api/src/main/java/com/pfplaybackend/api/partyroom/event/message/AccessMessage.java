package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.application.dto.PartymemberDto;
import com.pfplaybackend.api.partyroom.domain.enums.AccessType;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccessMessage implements Serializable {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private AccessType accessType;
    private PartymemberDto partymember;
}