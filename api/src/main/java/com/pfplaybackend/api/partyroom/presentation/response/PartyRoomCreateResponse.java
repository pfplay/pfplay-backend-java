package com.pfplaybackend.api.partyroom.presentation.response;

import com.pfplaybackend.api.entity.PartyRoom;
import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.enums.PartyRoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyRoomCreateResponse {
    private Long id;
    private String name;
    private String introduce;
    private String domain;
    private Integer djingLimit;
    @Schema(implementation = PartyRoomType.class)
    private PartyRoomType type;
    @Schema(implementation = PartyRoomStatus.class)
    private PartyRoomStatus status;

    public static PartyRoomCreateResponse toResponse(PartyRoom partyRoom) {
        return PartyRoomCreateResponse
                .builder()
                .id(partyRoom.getId())
                .name(partyRoom.getName())
                .introduce(partyRoom.getIntroduce())
                .domain(partyRoom.getIntroduce())
                .djingLimit(partyRoom.getDjingLimit())
                .type(partyRoom.getType())
                .status(partyRoom.getStatus())
                .build();
    }
}
