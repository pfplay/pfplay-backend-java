package com.pfplaybackend.api.partyroom.domain.value;

import com.pfplaybackend.api.partyroom.domain.enums.PartyroomGrade;
import lombok.Getter;

@Getter
public class PromoteInfo {
    private PartyroomGrade promotePrevGrade;
    private PartyroomGrade promoteNextGrade;
}
