package com.pfplaybackend.api.user.domain.model.domain;

import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.model.data.GuestData;
import com.pfplaybackend.api.user.domain.model.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.model.value.UserId;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class Guest extends User {

    final private String agent;

    public Guest(String agent) {
        super(AuthorityTier.GT);
        this.agent = agent;
    }

    public Guest(UserId userId, String agent) {
        super(userId, AuthorityTier.GT);
        this.agent = agent;
    }

    static public Guest create(String agent) {
        return new Guest(agent);
    }

    static public Guest createWithFixedUserId(UserId userId, String agent) {
        return new Guest(userId, agent);
    }

    // Guest Aggregate Operation
    public Guest initiateProfile(Profile profile) {
        return this.toBuilder()
                .profile(profile)
                .isProfileUpdated(true)
                .build();
    }

    public GuestData toData() {
        return GuestData.builder()
                .userId(this.userId)
                .authorityTier(this.authorityTier)
                .profileData(this.profile.toData())
                .isProfileUpdated(this.isProfileUpdated)
                .agent(this.agent)
                .build();
    }

    @Override
    public ProfileSummaryDto getProfileSummary() {
        return ProfileSummaryDto.builder()
                .nickName(this.profile.getNickname())
                .introduction(this.profile.getIntroduction())
                .avatarBodyId(this.profile.getAvatarBodyUri().getAvatarBodyUri())
                .avatarFaceUri(this.profile.getAvatarFaceUri().getAvatarFaceUri())
                .walletAddress(this.profile.getWalletAddress().getWalletAddress())
                .build();
    }
}