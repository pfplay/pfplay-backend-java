package com.pfplaybackend.api.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionDto {
    @Schema(description = "프로필 설정")
    private Boolean settingProfile;
    @Schema(description = "파티목록 화면")
    private Boolean showPartyListDisplay;
    @Schema(description = "메인 스테이 입장")
    private Boolean enterMainStage;
    @Schema(description = "채팅")
    private Boolean chat;
    @Schema(description = "플레이리스트 생성")
    private Boolean createPlayList;
    @Schema(description = "DJ 대기열 등록")
    private Boolean createWaitDj;
    @Schema(description = "파티룸 입장")
    private Boolean enterPartyRoom;
    @Schema(description = "파티룸 생성")
    private Boolean createPartyRoom;
    @Schema(description = "계급 권한")
    private Boolean admin;
    @Schema(description = "계급 권한")
    private Boolean communityManager;
    @Schema(description = "계급 권한")
    private Boolean moderator;
    @Schema(description = "계급 권한")
    private Boolean clubber;
    @Schema(description = "계급 권한")
    private Boolean listener;

}
