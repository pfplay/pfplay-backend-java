package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.domain.annotation.AggregateRoot;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.enums.FaceSourceType;
import com.pfplaybackend.api.user.domain.value.*;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AggregateRoot
@DynamicUpdate
@DynamicInsert
@Table(
        name = "MEMBER",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_email", columnNames = {"email"})
        }
)
@Getter
@Entity
@DiscriminatorValue("MEMBER")
public class MemberData extends UserAccountData {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private ProviderType providerType;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapKey(name = "activityType")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<ActivityType, ActivityData> activityDataMap;

    protected MemberData() {}

    @Builder
    public MemberData(UserId userId, AuthorityTier authorityTier, String email, ProviderType providerType, ProfileData profileData, boolean isProfileUpdated, Map<ActivityType, ActivityData> activityDataMap,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(userId, authorityTier, profileData, isProfileUpdated, createdAt, updatedAt);
        this.email = email;
        this.providerType = providerType;
        this.activityDataMap = activityDataMap;
    }

    public static MemberData create(String email, ProviderType providerType) {
        return MemberData.builder()
                .userId(new UserId())
                .email(email)
                .authorityTier(AuthorityTier.AM)
                .providerType(providerType)
                .isProfileUpdated(false)
                .build();
    }

    public static MemberData createWithFixedUserId(UserId userId, String email, ProviderType providerType) {
        return MemberData.builder()
                .userId(userId)
                .email(email)
                .authorityTier(AuthorityTier.AM)
                .providerType(providerType)
                .isProfileUpdated(false)
                .build();
    }

    @Override
    public boolean isGuest() {
        return false;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    public void initializeProfile(ProfileData profileData) {
        this.profileData = profileData;
    }

    public void initializeActivityMap(Map<ActivityType, ActivityData> activityDataMap) {
        this.activityDataMap = activityDataMap;
    }

    public void updateProfileBio(String nickName, String introduction) {
        this.profileData.updateBio(nickName, introduction);
        this.isProfileUpdated = true;
    }

    public void updateAvatarBody(AvatarBodyUri bodyUri, int combinePositionX, int combinePositionY) {
        this.profileData.updateAvatarBody(bodyUri, combinePositionX, combinePositionY);
    }

    public void updateAvatarFace(AvatarFaceUri avatarFaceUri) {
        this.profileData.updateAvatarFaceSingleBody(avatarFaceUri);
    }

    public void updateAvatarFace(AvatarFaceUri avatarFaceUri, FaceSourceType sourceType,
                                 double offsetX, double offsetY, double scale) {
        this.profileData.updateAvatarFaceWithTransform(
                avatarFaceUri, sourceType, offsetX, offsetY, scale);
    }

    public void updateAvatarIcon(AvatarIconUri avatarIconUri) {
        this.profileData.updateAvatarIcon(avatarIconUri);
    }

    public void updateWalletAddress(WalletAddress walletAddress) {
        this.profileData.updateWalletAddress(walletAddress);
        this.authorityTier = AuthorityTier.FM;
    }

    public void updateDjScore(int deltaScore) {
        ActivityData djActivity = this.activityDataMap.get(ActivityType.DJ_PNT);
        djActivity.addScore(deltaScore);
    }

    @Override
    public ProfileSummary getProfileSummary() {
        List<ActivitySummary> activitySummaries = this.activityDataMap.values().stream()
                .map(a -> new ActivitySummary(a.getActivityType(), a.getScore().getValue()))
                .toList();

        return buildProfileSummary(activitySummaries);
    }
}
