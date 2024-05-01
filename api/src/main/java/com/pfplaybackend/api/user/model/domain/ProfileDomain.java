package com.pfplaybackend.api.user.model.domain;

import com.pfplaybackend.api.user.model.entity.Profile;
import com.pfplaybackend.api.user.model.value.AvatarBodyId;
import com.pfplaybackend.api.user.model.value.AvatarFaceUri;
import com.pfplaybackend.api.user.model.value.UserId;
import com.pfplaybackend.api.user.model.value.WalletAddress;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ProfileDomain {
    private Long id;
    private final UserId userId;
    private final String nickname;
    private final String introduction;
    private AvatarFaceUri avatarFaceUrl;
    private AvatarBodyId avatarBodyId;
    private WalletAddress walletAddress;

    public ProfileDomain(UserId userId) {
        this.id = null;
        this.userId = userId;
        this.nickname = null;
        this.introduction = null;
        this.avatarFaceUrl = null;
        this.avatarBodyId = null;
        this.walletAddress = null;
    }

    Profile toEntity() {
        return Profile.builder()
                .userId(this.userId)
                .build();
    }
}
