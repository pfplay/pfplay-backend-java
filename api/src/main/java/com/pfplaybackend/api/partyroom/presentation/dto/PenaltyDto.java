package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomUser;
import com.pfplaybackend.api.partyroom.domain.value.PenaltyInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PenaltyDto {
    private String message;
    private PartyroomUser fromUser;
    private PartyroomUser toUser;
    private PenaltyInfo penaltyInfo;
}