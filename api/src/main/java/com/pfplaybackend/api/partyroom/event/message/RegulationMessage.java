package com.pfplaybackend.api.partyroom.event.message;

import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.enums.GradeType;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.domain.enums.MotionType;
import com.pfplaybackend.api.partyroom.domain.enums.RegulationType;
import com.pfplaybackend.api.partyroom.domain.value.PartymemberId;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegulationMessage {
    private PartyroomId partyroomId;
    private MessageTopic eventType;
    private RegulationType regulationType;
    private Map<String, Object> member;

    public static RegulationMessage from(PartyroomId partyroomId, RegulationType regulationType,
                                         PartymemberId partymemberId, GradeType prevGradeType, GradeType currGradeType) {
        Map<String, Object> member = new HashMap<>();
        member.put("memberId", partymemberId.getId());
        member.put("prevGradeType", prevGradeType);
        member.put("currGradeType", currGradeType);

        return new RegulationMessage(
                partyroomId,
                MessageTopic.REGULATION,
                regulationType,
                member
        );
    }
}
