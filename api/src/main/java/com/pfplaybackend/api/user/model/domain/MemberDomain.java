package com.pfplaybackend.api.user.model.domain;

import com.pfplaybackend.api.config.oauth2.enums.ProviderType;
import com.pfplaybackend.api.user.model.domain.operations.MemberOperation;
import com.pfplaybackend.api.user.model.entity.Activity;
import com.pfplaybackend.api.user.model.entity.Member;
import com.pfplaybackend.api.user.model.entity.Profile;
import com.pfplaybackend.api.user.model.enums.ActivityType;
import com.pfplaybackend.api.user.model.enums.AuthorityTier;
import com.pfplaybackend.api.user.model.value.UserId;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class MemberDomain extends UserDomain implements MemberOperation {

    private final String email;
    private final ProviderType providerType;
    private ProfileDomain profileDomain;
    private boolean isProfileUpdated;
    private Map<ActivityType, ActivityDomain> activityDomains;

    public MemberDomain(String email, AuthorityTier authorityTier, ProviderType providerType) {
        super(authorityTier);
        this.email = email;
        this.providerType = providerType;
    }

    @Builder
    public MemberDomain(UserId userId, String email, AuthorityTier authorityTier, ProviderType providerType, ProfileDomain profileDomain, boolean isProfileUpdated, Map<ActivityType, ActivityDomain> activityDomains) {
        super(userId, authorityTier);
        this.email = email;
        this.providerType = providerType;
        this.profileDomain = profileDomain;
        this.isProfileUpdated = isProfileUpdated;
        this.activityDomains = activityDomains;
    }

    public Member toEntity() {
        Map<ActivityType, Activity > transformedMap = this.activityDomains.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toEntity()
                ));

        return Member.builder()
                .userId(this.userId)
                .email(this.email)
                .profile(this.ProfileDomain.toEntity())
                .isProfileUpdated(this.isProfileUpdated)
                .activities(transformedMap)
                .authorityTier(this.authorityTier)
                .providerType(this.providerType)
                .build();
    }

    static public MemberDomain create(String email, ProviderType providerType) {
        MemberDomain memberDomain = new MemberDomain(email, AuthorityTier.AM, providerType);
        memberDomain.initializeProfileAndActivity();
        return memberDomain;
    }

    public void initializeProfileAndActivity() {
        ProfileDomain profileDomain = new ProfileDomain(this.userId);
        Map<ActivityType, ActivityDomain> activityDomains = new HashMap<>();
        for (ActivityType activityType : ActivityType.values()) {
            activityDomains.put(activityType, new ActivityDomain(this.userId, activityType, 0));
        }
        this.ProfileDomain = profileDomain;
        this.activityDomains = activityDomains;
    }

    @Override
    public MemberDomain updateActivity() {
        return null;
    }

    @Override
    public MemberDomain updateProfile(com.pfplaybackend.api.user.model.domain.ProfileDomain profileDomain) {
        return null;
    }

    @Override
    public MemberDomain upgradeAuthorityTier() {
        return MemberDomain.builder()
                .userId(this.userId)
                .email(this.email)
                .authorityTier(AuthorityTier.FM)
                .providerType(this.providerType)
                .build();
    }
}
