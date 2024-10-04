package com.pfplaybackend.api.partyroom.presentation.payload.request.regulation;

import com.pfplaybackend.api.partyroom.domain.enums.PenaltyType;
import lombok.Getter;

@Getter
public class PunishPenaltyRequest {
    private PenaltyType penaltyType;
    private String reason;
}