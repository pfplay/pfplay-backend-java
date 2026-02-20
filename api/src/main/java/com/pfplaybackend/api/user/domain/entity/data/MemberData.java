package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.profile.domain.enums.AvatarCompositionType;
import com.pfplaybackend.api.profile.adapter.in.web.dto.request.AvatarFaceRequest;
import com.pfplaybackend.api.user.application.dto.command.UpdateBioCommand;
import com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto;
import com.pfplaybackend.api.user.application.dto.shared.AvatarBodyDto;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.user.domain.value.*;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DynamicUpdate
@DynamicInsert
@Table(
        name = "MEMBER",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_email", columnNames = {"email"})
        }
)
@Getter
@Setter
@Entity
public class MemberData extends BaseEntity {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "user_id")),
    })
    private UserId userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityTier authorityTier;

    @Column(nullable = false)
    private ProviderType providerType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private ProfileData profileData;

    @Column(nullable = false)
    private boolean isProfileUpdated;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapKey(name = "activityType")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<ActivityType, ActivityData> activityDataMap;

    public MemberData() {}

    @Builder
    public MemberData(UserId userId, AuthorityTier authorityTier, String email, ProviderType providerType, ProfileData profileData, boolean isProfileUpdated, Map<ActivityType, ActivityData> activityDataMap,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.authorityTier = authorityTier;
        this.profileData = profileData;
        this.isProfileUpdated = isProfileUpdated;
        this.email = email;
        this.providerType = providerType;
        this.activityDataMap = activityDataMap;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public void initializeProfile(ProfileData profileData) {
        this.profileData = profileData;
    }

    public void initializeActivityMap(Map<ActivityType, ActivityData> activityDataMap) {
        this.activityDataMap = activityDataMap;
    }

    public void updateProfileBio(UpdateBioCommand command) {
        this.profileData.updateBio(command.getNickName(), command.getIntroduction());
        this.isProfileUpdated = true;
    }

    public void updateAvatarBody(AvatarBodyDto avatarBodyDto) {
        this.profileData.updateAvatarBody(
                new AvatarBodyUri(avatarBodyDto.getResourceUri()),
                avatarBodyDto.getCombinePositionX(),
                avatarBodyDto.getCombinePositionY());
    }

    public void updateAvatarFace(AvatarFaceUri avatarFaceUri) {
        this.profileData.updateAvatarFaceSingleBody(avatarFaceUri);
    }

    public void updateAvatarFace(AvatarFaceUri avatarFaceUri, AvatarFaceRequest avatarFaceRequest) {
        this.profileData.updateAvatarFaceWithTransform(
                avatarFaceUri,
                avatarFaceRequest.getSourceType(),
                avatarFaceRequest.getTransform().getOffsetX(),
                avatarFaceRequest.getTransform().getOffsetY(),
                avatarFaceRequest.getTransform().getScale());
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

    public ProfileSummaryDto getProfileSummary() {
        List<ActivitySummaryDto> activitySummaries = this.activityDataMap.values().stream()
                .map(a -> ActivitySummaryDto.builder()
                        .activityType(a.getActivityType())
                        .score(a.getScore().getValue())
                        .build())
                .collect(Collectors.toList());

        return ProfileSummaryDto.builder()
                .nickname(this.profileData.getNicknameValue())
                .introduction(this.profileData.getIntroduction())
                .avatarBodyUri(this.profileData.getAvatarBodyUri().getAvatarBodyUri())
                .avatarFaceUri(this.profileData.getAvatarFaceUri().getAvatarFaceUri())
                .avatarIconUri(this.profileData.getAvatarIconUri().getAvatarIconUri())
                .avatarCompositionType(this.profileData.getAvatarCompositionType())
                .combinePositionX(this.profileData.getCombinePositionX())
                .combinePositionY(this.profileData.getCombinePositionY())
                .offsetX(this.profileData.getOffsetX())
                .offsetY(this.profileData.getOffsetY())
                .scale(this.profileData.getScale())
                .walletAddress(this.profileData.getWalletAddress().getWalletAddress())
                .activitySummaries(activitySummaries)
                .build();
    }
}