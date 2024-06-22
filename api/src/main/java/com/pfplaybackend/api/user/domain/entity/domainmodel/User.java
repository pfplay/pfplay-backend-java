package com.pfplaybackend.api.user.domain.entity.domainmodel;

import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@EqualsAndHashCode
@ToString
@SuperBuilder(toBuilder = true)
public abstract class User {

    protected UserId userId;
    protected AuthorityTier authorityTier;
    protected Profile profile;
    protected boolean isProfileUpdated;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    User(AuthorityTier authorityTier) {
        this.userId = new UserId();
        this.authorityTier = authorityTier;
        this.profile = null;
        this.isProfileUpdated = false;
        this.createdAt = null;
        this.updatedAt = null;
    }

    User(UserId userId, AuthorityTier authorityTier) {
        this.userId = Objects.requireNonNullElseGet(userId, UserId::new);
        this.authorityTier = authorityTier;
        this.profile = null;
        this.isProfileUpdated = false;
        this.createdAt = null;
        this.updatedAt = null;
    }

    User(UserId userId, AuthorityTier authorityTier, Profile profile, boolean isProfileUpdated) {
        this.userId = Objects.requireNonNullElseGet(userId, UserId::new);
        this.authorityTier = authorityTier;
        this.profile = profile;
        this.isProfileUpdated = isProfileUpdated;
        this.createdAt = null;
        this.updatedAt = null;
    }

    User(UserId userId, AuthorityTier authorityTier, Profile profile, boolean isProfileUpdated, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = Objects.requireNonNullElseGet(userId, UserId::new);
        this.authorityTier = authorityTier;
        this.profile = profile;
        this.isProfileUpdated = isProfileUpdated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public abstract ProfileSummaryDto getProfileSummary();
}

