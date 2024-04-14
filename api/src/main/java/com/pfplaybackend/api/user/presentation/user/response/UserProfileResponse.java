package com.pfplaybackend.api.user.presentation.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserProfileResponse {
    private String nickname;
    private String introduction;
    private String faceUrl;
    private Integer bodyId;
    private String bodyUrl;
    private String walletAddress;

    @Builder
    public UserProfileResponse(String email, String nickname, String introduction, String faceUrl, Integer bodyId, String bodyUrl, String walletAddress) {
        this.nickname = nickname;
        this.introduction = introduction;
        this.faceUrl = faceUrl;
        this.bodyId = bodyId;
        this.bodyUrl = bodyUrl;
        this.walletAddress = walletAddress;
    }
}
