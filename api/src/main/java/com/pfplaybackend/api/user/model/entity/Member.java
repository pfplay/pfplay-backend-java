package com.pfplaybackend.api.user.model.entity;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.config.oauth2.enums.ProviderType;
import com.pfplaybackend.api.user.model.domain.MemberDomain;
import com.pfplaybackend.api.user.model.enums.ActivityType;
import com.pfplaybackend.api.user.model.enums.AuthorityTier;
import com.pfplaybackend.api.user.model.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;
import java.util.Map;

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
public class Member extends BaseEntity {

    @EmbeddedId
    private UserId userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityTier authorityTier;

    @Column(nullable = false)
    private ProviderType providerType;

    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;

    private boolean isProfileUpdated;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapKey(name = "activityType")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<ActivityType, Activity> activities;

    public Member() { }

    @Builder
    public Member(UserId userId, String email, AuthorityTier authorityTier, ProviderType providerType, Profile profile, boolean isProfileUpdated, Map<ActivityType, Activity> activities) {
        this.userId = userId;
        this.email = email;
        this.authorityTier = authorityTier;
        this.providerType = providerType;
        this.profile = profile;
        this.isProfileUpdated = isProfileUpdated;
        this.activities = activities;
    }

    public MemberDomain toDomain() {
        // TODO this.activities.toDomain();
        // TODO this.profile.toDomain();
        return MemberDomain.builder()
                .userId(this.userId)
                .email(this.email)
                .authorityTier(this.authorityTier)
                .providerType(this.providerType)
                .build();
    }
}