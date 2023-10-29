package com.pfplaybackend.api.partyroom.presentation.dto;

import com.pfplaybackend.api.partyroom.enums.PartyPermissionRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "파티룸 생성시 default 권한")
public class PartyRoomPermissionDto {

    @Schema(description = "파티룸 권한")
    private PartyPermissionRole authority;

    @Schema(description = "파티 정보 수정")
    private Boolean partyInfoFetch;

    @Schema(description = "파티룸 폐쇄")
    private Boolean partyClose;

    @Schema(description = "공지 작성/삭제/수정")
    private Boolean notice;

    @Schema(description = "클러버 권한 부여")
    private Boolean giveToClubber;

    @Schema(description = "채팅 삭제")
    private Boolean chatDelete;

    @Schema(description = "클러버 패널티 30초 채팅 금지")
    private Boolean chatLimitBanToClubber;

    @Schema(description = "클러버 패널티 30초 킥(재입장 가능)")
    private Boolean kickToClubber;

    @Schema(description = "클러버 패널티 30초 킥(재입장 불가능)")
    private Boolean banToClubber;

    @Schema(description = "채팅 차단")
    private Boolean chatBan;

    @Schema(description = "DJ 대기열 잠금")
    private Boolean djWaitLock;

    @Schema(description = "신규 DJ 추가/삭제")
    private Boolean newDj;

    @Schema(description = "음악 스킵")
    private Boolean musicSkip;

    @Schema(description = "영상 길이 제한")
    private Boolean videoLengthLimit;

}
