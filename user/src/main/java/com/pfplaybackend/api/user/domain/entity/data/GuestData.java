package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.domain.annotation.AggregateRoot;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@AggregateRoot
@DynamicUpdate
@DynamicInsert
@Table(name = "GUEST")
@Getter
@Entity
@DiscriminatorValue("GUEST")
public class GuestData extends UserAccountData {

    private String agent;

    protected GuestData() {}

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
