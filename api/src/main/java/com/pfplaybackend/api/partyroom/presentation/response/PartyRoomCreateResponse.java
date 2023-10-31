package com.pfplaybackend.api.partyroom.presentation.response;

import com.pfplaybackend.api.partyroom.enums.PartyRoomStatus;
import com.pfplaybackend.api.partyroom.enums.PartyRoomType;
import com.pfplaybackend.api.partyroom.presentation.dto.PartyRoomPermissionDto;
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
    private PartyRoomCreateAdminInfo admin;
    @Schema(implementation = PartyRoomPermissionDto.class)
    private PartyRoomPermissionDto partyRoomPermission;
    @Schema(description = "파티룸 신규 생성 여부")
    private Boolean isNew;

}
