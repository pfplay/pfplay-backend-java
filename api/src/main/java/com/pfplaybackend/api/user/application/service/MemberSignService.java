package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.config.oauth2.enums.ProviderType;
import com.pfplaybackend.api.config.oauth2.properties.OAuth2ProviderConfig;
import com.pfplaybackend.api.config.oauth2.model.OAuth2Redirection;
import com.pfplaybackend.api.user.domain.model.data.MemberData;
import com.pfplaybackend.api.user.domain.model.domain.Activity;
import com.pfplaybackend.api.user.domain.model.domain.Member;
import com.pfplaybackend.api.user.domain.model.domain.Profile;
import com.pfplaybackend.api.user.domain.model.enums.ActivityType;
import com.pfplaybackend.api.user.presentation.payload.request.SignMemberRequest;
import com.pfplaybackend.api.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberSignService {

    private final OAuth2ProviderConfig oauth2ProviderConfig;
    private final MemberRepository memberRepository;
    private final UserProfileService userProfileService;
    private final UserActivityService userActivityService;

    public String getOAuth2RedirectUri(SignMemberRequest request) {
        OAuth2Redirection oauth2Redirection = OAuth2Redirection.create(oauth2ProviderConfig.getProviders(), request.getOauth2Provider(), request.getRedirectLocation());
        return oauth2Redirection.getUrl();
    }

    @Transactional
    public Member getMemberOrCreate(String email) {
        return tryFindMember(email).orElseGet(() -> registerNewMember(email)).toDomain();
    }

    private Optional<MemberData> tryFindMember(String email) {
        return memberRepository.findByEmail(email);
    }

    private MemberData registerNewMember(String email) {
        // TODO Parse ProviderType
        Member member = Member.create(email, ProviderType.GOOGLE);
        Profile profile = userProfileService.createProfileForMember(member);
        Map<ActivityType, Activity> activityMap = userActivityService.createUserActivities(member);
        Member updatedMember = member
                .initializeProfile(profile)
                .initializeActivityMap(activityMap);
        return memberRepository.save(updatedMember.toData());
    }
}