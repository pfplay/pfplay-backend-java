package com.pfplaybackend.api.partyroom.model.value;

import com.pfplaybackend.api.common.enums.PartyroomGrade;
import lombok.Getter;

@Getter
public class PromoteInfo {
    private PartyroomGrade promotePrevGrade;
    private PartyroomGrade promoteNextGrade;
}
