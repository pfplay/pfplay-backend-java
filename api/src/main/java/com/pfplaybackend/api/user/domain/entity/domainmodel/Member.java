package com.pfplaybackend.api.user.domain.entity.domainmodel;

import com.pfplaybackend.api.config.oauth2.enums.ProviderType;
import com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.UserId;
import com.pfplaybackend.api.user.domain.value.WalletAddress;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class Member extends User {

    private final String email;
    private final ProviderType providerType;
    private Map<ActivityType, Activity> activityMap;

    public Member(String email, AuthorityTier authorityTier, ProviderType providerType) {
        super(authorityTier);
        this.email = email;
        this.providerType = providerType;
    }

    public Member(UserId userId, String email, AuthorityTier authorityTier, ProviderType providerType) {
        super(userId, authorityTier);
        this.email = email;
        this.providerType = providerType;
    }

    public Member(UserId userId, AuthorityTier authorityTier, String email, ProviderType providerType, Profile profile, boolean isProfileUpdated, Map<ActivityType, Activity> activityMap,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(userId, authorityTier, profile, isProfileUpdated);
        this.email = email;
        this.providerType = providerType;
        this.activityMap = activityMap;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    static public Member create(String email, ProviderType providerType) {
        return new Member(email, AuthorityTier.AM, providerType);
    }

    static public Member createWithFixedUserId(UserId userId, String email, ProviderType providerType) {
        return new Member(userId, email, AuthorityTier.AM, providerType);
    }

    public MemberData toData() {
        Map<ActivityType, ActivityData> transformedMap = this.activityMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toData()
                ));

        return MemberData.builder()
                .userId(this.userId)
                .authorityTier(this.authorityTier)
                .email(this.email)
                .profileData(this.profile.toData())
                .isProfileUpdated(this.isProfileUpdated)
                .activityDataMap(transformedMap)
                .providerType(this.providerType)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    public Member initializeProfile(Profile profile) {
        return this.toBuilder()
                .profile(profile)
                .isProfileUpdated(false)
                .build();
    }

    public Member initializeActivityMap(Map<ActivityType, Activity> activityMap) {
        return this.toBuilder()
                .activityMap(activityMap)
                .build();
    }

    public Member updateProfileBio(UpdateBioCommand updateBioCommand) {
        Profile newProfile = this.profile
                .withNickname(updateBioCommand.getNickName())
                .withIntroduction(updateBioCommand.getIntroduction());

        return this.toBuilder()
                .profile(newProfile)
                .isProfileUpdated(true)
                .build();
    }

    public Member updateAvatarBody(AvatarBodyUri avatarBodyUri, AvatarBodyDto avatarBodyDto) {
        Profile newProfile = this.profile
                .withAvatarBodyUri(avatarBodyUri)
                .withCombinePositionX(avatarBodyDto.getCombinePositionX())
                .withCombinePositionY(avatarBodyDto.getCombinePositionY());

        return this.toBuilder()
                .profile(newProfile)
                .build();
    }

    public Member updateAvatarFace(AvatarFaceUri avatarFaceUri) {
        Profile newProfile = this.profile
                .withAvatarFaceUri(avatarFaceUri);

        return this.toBuilder()
                .profile(newProfile)
                .build();
    }

    public Member updateWalletAddress(WalletAddress walletAddress) {
        Profile newProfile = this.profile
                .withWalletAddress(walletAddress);

        return this.toBuilder()
                .profile(newProfile)
                .authorityTier(AuthorityTier.FM)
                .build();
    }

    @Override
    public ProfileSummaryDto getProfileSummary() {
        List<ActivitySummaryDto> activitySummaries = this.activityMap.values().stream()
                .map(activity -> ActivitySummaryDto.builder()
                        .activityType(activity.getActivityType())
                        .score(activity.getScore())
                        .build())
                .collect(Collectors.toList());

        return ProfileSummaryDto.builder()
                .nickname(this.profile.getNickname())
                .introduction(this.profile.getIntroduction())
                .avatarBodyUri(this.profile.getAvatarBodyUri().getAvatarBodyUri())
                .avatarFaceUri(this.profile.getAvatarFaceUri().getAvatarFaceUri())
                .walletAddress(this.profile.getWalletAddress().getWalletAddress())
                .activitySummaries(activitySummaries)
                .build();
    }
}
