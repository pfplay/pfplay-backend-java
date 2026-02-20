package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@DynamicUpdate
@DynamicInsert
@Table(name = "GUEST")
@Getter
@Setter
@Entity
public class GuestData extends BaseEntity {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "uid", column = @Column(name = "user_id")),
    })
    private UserId userId;

    private String agent;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityTier authorityTier;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private ProfileData profileData;

    @Column(nullable = false)
    private boolean isProfileUpdated;

    public GuestData() {}

    @Builder
    public GuestData(UserId userId, AuthorityTier authorityTier, String agent, ProfileData profileData, boolean isProfileUpdated,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.authorityTier = authorityTier;
        this.profileData = profileData;
        this.isProfileUpdated = isProfileUpdated;
        this.agent = agent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static GuestData create() {
        return GuestData.builder()
                .userId(new UserId())
                .authorityTier(AuthorityTier.GT)
                .isProfileUpdated(false)
                .build();
    }

    public static GuestData createWithFixedUserId(UserId userId, String agent) {
        return GuestData.builder()
                .userId(userId)
                .agent(agent)
                .authorityTier(AuthorityTier.GT)
                .isProfileUpdated(false)
                .build();
    }

    public void initiateProfile(ProfileData profileData) {
        this.profileData = profileData;
        this.isProfileUpdated = true;
    }

    public ProfileSummaryDto getProfileSummary() {
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
                .build();
    }
}
