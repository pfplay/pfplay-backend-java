package com.pfplaybackend.api.user.domain.entity.domainmodel;

import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.domain.value.WalletAddress;
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
    private AvatarFaceUri avatarFaceUri;
    @With
    private AvatarBodyUri avatarBodyUri;
    @With
    private WalletAddress walletAddress;

    public Profile(UserId userId) {
        this.userId = userId;
        this.nickname = null;
        this.introduction = null;
        this.avatarFaceUri = null;
        this.avatarBodyUri = null;
        this.walletAddress = null;
    }

    @Builder
    public Profile(Long id, UserId userId, String nickname, String introduction, AvatarFaceUri avatarFaceUrl, AvatarBodyUri avatarBodyUri, WalletAddress walletAddress) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.introduction = introduction;
        this.avatarBodyUri = avatarBodyUri;
        this.avatarFaceUri = avatarFaceUrl;
        this.walletAddress = walletAddress;
    }

    ProfileData toData() {
        return ProfileData.builder()
                .id(this.id)
                .userId(this.userId)
                .nickname(this.nickname)
                .introduction(this.introduction)
                .avatarBodyUri(this.avatarBodyUri)
                .avatarFaceUri(this.avatarFaceUri)
                .walletAddress(this.walletAddress)
                .build();
    }
}
