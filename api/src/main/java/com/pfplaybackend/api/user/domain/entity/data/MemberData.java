package com.pfplaybackend.api.user.domain.entity.data;

import com.pfplaybackend.api.common.entity.BaseEntity;
import com.pfplaybackend.api.config.oauth2.enums.ProviderType;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Activity;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.value.UserId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
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
    private UserId userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityTier authorityTier;

    @Column(nullable = false)
    private ProviderType providerType;

    @OneToOne(cascade = CascadeType.ALL)
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

    public Member toDomain() {
        Map<ActivityType, Activity> transformedMap = this.activityDataMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toDomain()
                ));

        return Member.builder()
                .userId(this.userId)
                .authorityTier(this.authorityTier)
                .profile(this.profileData.toDomain())
                .activityMap(transformedMap)
                .isProfileUpdated(this.isProfileUpdated)
                .email(this.email)
                .providerType(this.providerType)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}