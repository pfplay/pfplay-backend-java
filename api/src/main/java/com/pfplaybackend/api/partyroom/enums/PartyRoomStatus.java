package com.pfplaybackend.api.partyroom.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파티룸 활성 상태")
public enum PartyRoomStatus {
    @Schema(description = "활성")
    ACTIVE,
    @Schema(description = "비활성")
    INACTIVE
}
