package com.pfplaybackend.api.partyroom.presentation.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;



@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파티룸 입장")
public class PartyRoomJoinResultDto {
    @Schema(description = "해당 파티룸 첫 접속 여부")
    private boolean hasJoined;
    @Schema(description = "파티룸")
    private PartyRoomDto partyRoom;
    @Schema(description = "파티룸 권한")
    private PartyPermissionDto partyPermission;
    @Schema(description = "밴 여부")
    private PartyRoomBanDto partyRoomBan;

}
