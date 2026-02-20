package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@DynamicUpdate
@DynamicInsert
@Table(name = "USER_ACCOUNT")
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class UserAccountData extends BaseEntity {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "user_id")),
    })
    protected UserId userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected AuthorityTier authorityTier;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    protected ProfileData profileData;

    @Column(nullable = false)
    protected boolean isProfileUpdated;

    protected UserAccountData() {}

    protected UserAccountData(UserId userId, AuthorityTier authorityTier, ProfileData profileData,
                              boolean isProfileUpdated, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.authorityTier = authorityTier;
        this.profileData = profileData;
        this.isProfileUpdated = isProfileUpdated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public abstract boolean isGuest();

    protected ProfileSummaryDto buildProfileSummary(java.util.List<com.pfplaybackend.api.user.application.dto.shared.ActivitySummaryDto> activitySummaries) {
        var bio = this.profileData.getBio();
        var avatar = this.profileData.getAvatarSetting();
        return new ProfileSummaryDto(
                bio != null ? bio.getNicknameValue() : null,
                bio != null ? bio.getIntroduction() : null,
                avatar.getAvatarBodyUri().getAvatarBodyUri(),
                avatar.getAvatarCompositionType(),
                avatar.getCombinePositionX(),
                avatar.getCombinePositionY(),
                avatar.getOffsetX(),
                avatar.getOffsetY(),
                avatar.getScale(),
                avatar.getAvatarFaceUri().getAvatarFaceUri(),
                avatar.getAvatarIconUri().getAvatarIconUri(),
                this.profileData.getWalletAddress().getWalletAddress(),
                activitySummaries
        );
    }

    public ProfileSummaryDto getProfileSummary() {
        return buildProfileSummary(java.util.List.of());
    }

    public String getEmail() {
        return null;
    }
}
