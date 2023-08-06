package com.pfplaybackend.api.user.presentation.request;

import lombok.Getter;

@Getter
public class ProfileUpdateRequest {
    private String nickname;
    private String introduction;
    private String faceUrl;
    private Integer bodyId;
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
