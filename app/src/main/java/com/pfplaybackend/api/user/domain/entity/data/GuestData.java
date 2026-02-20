package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
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
@DiscriminatorValue("GUEST")
public class GuestData extends UserAccountData {

    private String agent;

    public GuestData() {}

    @Builder
    public GuestData(UserId userId, AuthorityTier authorityTier, String agent, ProfileData profileData, boolean isProfileUpdated,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(userId, authorityTier, profileData, isProfileUpdated, createdAt, updatedAt);
        this.agent = agent;
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

    @Override
    public boolean isGuest() {
        return true;
    }

    public void initiateProfile(ProfileData profileData) {
        this.profileData = profileData;
        this.isProfileUpdated = true;
    }
}
