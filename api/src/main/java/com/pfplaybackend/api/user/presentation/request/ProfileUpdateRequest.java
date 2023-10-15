package com.pfplaybackend.api.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "사용자 요청 데이터")
@Getter
public class ProfileUpdateRequest {
    @Schema(description = "사용자 닉네임", example = "홍길동", required = true, type = "string")
    private String nickname;
    @Schema(description = "사용자 소개말", example = "안녕하세요", required = true, type = "string")
    private String introduction;
    @Schema(description = "사용자 face url", example = "https://assets.website-files.com/637be5d0f2736f32b8ad98cd/638e627f643cfa7dd56beb96_qIsIXihKZeVDop6AZWt1j6gxOnYZ_oGfr09PzlJDL_H4YWasvDrNuTXK8Qrmh0oL6ppWI3RaGU5vMif2gNwO38UdWWei4eZCNhbfdrVlm5qHV3zVYIk6qtBuFvdkoo0HexhmSmvn.jpeg", required = false, type = "string")
    private String faceUrl;
    @Schema(description = "사용자 body id", example = "1", required = false, type = "integer")
    private Integer bodyId;
    @Schema(description = "사용자 지갑 주소", example = "0xabcdefg", required = false, type = "string")
    private String walletAddress;

    public ProfileUpdateRequest() { }

    public ProfileUpdateRequest(String nickname, String introduction, String faceUrl, Integer bodyId, String walletAddress) {
        this.nickname = nickname;
        this.introduction = introduction;
        this.faceUrl = faceUrl;
        this.bodyId = bodyId;
        this.walletAddress = walletAddress;
    }
}
