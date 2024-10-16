package com.pfplaybackend.api.user.domain.entity.domainmodel;

import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.value.*;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

@Getter
public class Profile {

    private Long id;
    private final UserId userId;
    @With
    private final String nickname;
    @With
    private final String introduction;
    @With
    private AvatarBodyUri avatarBodyUri;
    @With
    private AvatarFaceUri avatarFaceUri;
    @With
    private AvatarIconUri avatarIconUri;
    @With
    private WalletAddress walletAddress;
    @With
    private int combinePositionX;
    @With
    private int combinePositionY;

    public Profile(UserId userId) {
        this.userId = userId;
        this.nickname = null;
        this.introduction = null;
        this.avatarBodyUri = null;
        this.avatarFaceUri = null;
        this.avatarIconUri = null;
        this.walletAddress = null;
        this.combinePositionX = 0;
        this.combinePositionY = 0;
    }

    @Builder
    public Profile(Long id, UserId userId, String nickname, String introduction,
                   AvatarBodyUri avatarBodyUri, AvatarFaceUri avatarFaceUrl, AvatarIconUri avatarIconUri, WalletAddress walletAddress,
                   int combinePositionX, int combinePositionY) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.introduction = introduction;
        this.avatarBodyUri = avatarBodyUri;
        this.avatarFaceUri = avatarFaceUrl;
        this.avatarIconUri = avatarIconUri;
        this.walletAddress = walletAddress;
        this.combinePositionX = combinePositionX;
        this.combinePositionY = combinePositionY;
    }

    ProfileData toData() {
        return ProfileData.builder()
                .id(this.id)
                .userId(this.userId)
                .nickname(this.nickname)
                .introduction(this.introduction)
                .avatarBodyUri(this.avatarBodyUri)
                .avatarFaceUri(this.avatarFaceUri)
                .avatarIconUri(this.avatarIconUri)
                .walletAddress(this.walletAddress)
                .combinePositionX(this.combinePositionX)
                .combinePositionY(this.combinePositionY)
                .build();
    }
}
