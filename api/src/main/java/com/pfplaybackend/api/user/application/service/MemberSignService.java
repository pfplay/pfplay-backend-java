package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.config.security.model.OAuth2Redirection;
import com.pfplaybackend.api.common.config.security.properties.OAuth2ProviderConfig;
import com.pfplaybackend.api.profile.application.service.UserProfileService;
import com.pfplaybackend.api.user.application.port.out.PlaylistSetupPort;
import com.pfplaybackend.api.profile.domain.ProfileData;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.adapter.in.web.payload.request.SignMemberRequest;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
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
    private final PlaylistSetupPort playlistSetupPort;

    public String getOAuth2RedirectUri(SignMemberRequest request, String redirectLocation) {
        OAuth2Redirection oauth2Redirection = OAuth2Redirection.create(oauth2ProviderConfig.getProviders(), request.getOauth2Provider(), redirectLocation);
        return oauth2Redirection.getUrl();
    }

    @Transactional
    public MemberData getMemberOrCreate(String email, ProviderType providerType) {
        return tryFindMember(email).orElseGet(() -> registerNewMember(email, providerType));
    }

    private Optional<MemberData> tryFindMember(String email) {
        return memberRepository.findByEmail(email);
    }

    private MemberData registerNewMember(String email, ProviderType providerType) {
        MemberData member = MemberData.create(email, providerType);
        ProfileData profile = userProfileService.createProfileDataForMember(member.getUserId());
        Map<ActivityType, ActivityData> activityMap = userActivityService.createUserActivities(member.getUserId());
        member.initializeProfile(profile);
        member.initializeActivityMap(activityMap);

        playlistSetupPort.createDefaultPlaylist(member.getUserId());

        return memberRepository.save(member);
    }
}
