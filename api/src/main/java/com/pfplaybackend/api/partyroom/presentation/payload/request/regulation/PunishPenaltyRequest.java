package com.pfplaybackend.api.partyroom.presentation.payload.request.regulation;

import com.pfplaybackend.api.partyroom.domain.enums.PenaltyType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class PunishPenaltyRequest {

    @NotNull(message = "PenaltyType is required.")
    private PenaltyType penaltyType;
    private String detail;

    @AssertTrue(message = "Reason of penalty is required")
    public boolean isDetailValid() {
        if(penaltyType.equals(PenaltyType.CHAT_MESSAGE_REMOVAL)) {
            String regex = "^\\d{13}:\\d+$";
            return detail.matches(regex);
        }else {
            return detail != null && detail.length() > 1;
        }
    }
}