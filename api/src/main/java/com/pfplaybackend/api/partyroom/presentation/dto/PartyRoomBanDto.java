package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.common.enums.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "파티룸 밴")
public class PartyRoomBanDto {
    @Schema(description = "밴 pk")
    private Long id;
    @Schema(description = "밴 당한 유저 아이디")
    private Long userId;
    @Schema(description = "밴")
    private Boolean ban;
    @Schema(description = "킥(30초 limit)")
    private Boolean kick;
    @Schema(description = "채팅 차단")
    private Boolean chat;
    @Schema(description = "사유")
    private String reason;
    @Schema(description = "유저 롤", implementation = Authority.class)
    private Authority authority;

}
