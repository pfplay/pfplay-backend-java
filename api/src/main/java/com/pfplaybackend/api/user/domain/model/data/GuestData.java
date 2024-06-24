package com.pfplaybackend.api.user.domain.model.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.user.domain.model.domain.Guest;
import com.pfplaybackend.api.user.domain.model.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.model.value.UserId;
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
    private UserId userId;

    private String agent;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityTier authorityTier;

    @OneToOne(cascade = CascadeType.ALL)
    private ProfileData profileData;

    @Column(nullable = false)
    private boolean isProfileUpdated;

    public GuestData() { }

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

    public Guest toDomain() {
        return Guest.builder()
                .userId(this.userId)
                .authorityTier(this.authorityTier)
                .profile(this.profileData.toDomain())
                .isProfileUpdated(this.isProfileUpdated)
                .agent(this.agent)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}