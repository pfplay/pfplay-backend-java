package com.pfplaybackend.api.user.domain.entity.domainmodel;

import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.profile.domain.enums.FaceSourceType;
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
    private AvatarCompositionType avatarCompositionType;
    @With
    private FaceSourceType faceSourceType;
    @With
    private int combinePositionX;
    @With
    private int combinePositionY;
    @With
    private double offsetX;
    @With
    private double offsetY;
    @With
    private double scale;

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
        this.offsetX = 0;
        this.offsetY = 0;
        this.scale = 0;
    }

    @Builder
    public Profile(Long id, UserId userId, String nickname, String introduction,
                   AvatarBodyUri avatarBodyUri, AvatarFaceUri avatarFaceUrl, AvatarIconUri avatarIconUri, WalletAddress walletAddress,
                   AvatarCompositionType avatarCompositionType, FaceSourceType faceSourceType,
                   int combinePositionX, int combinePositionY, double offsetX, double offsetY, double scale) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.introduction = introduction;
        this.avatarBodyUri = avatarBodyUri;
        this.avatarFaceUri = avatarFaceUrl;
        this.avatarIconUri = avatarIconUri;
        this.walletAddress = walletAddress;
        this.avatarCompositionType = avatarCompositionType;
        this.faceSourceType = faceSourceType;
        this.combinePositionX = combinePositionX;
        this.combinePositionY = combinePositionY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
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
                .avatarCompositionType(this.avatarCompositionType)
                .faceSourceType(this.faceSourceType)
                .combinePositionX(this.combinePositionX)
                .combinePositionY(this.combinePositionY)
                .offsetX(this.offsetX)
                .offsetY(this.offsetY)
                .scale(this.scale)
                .build();
    }
}
