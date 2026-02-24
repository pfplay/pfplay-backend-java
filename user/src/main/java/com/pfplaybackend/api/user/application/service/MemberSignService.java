package com.pfplaybackend.api.user.application.service;

import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import com.pfplaybackend.api.user.application.dto.command.SignMemberCommand;
import com.pfplaybackend.api.user.application.port.out.OAuth2RedirectPort;
import com.pfplaybackend.api.user.application.port.out.PlaylistSetupPort;
import com.pfplaybackend.api.user.domain.entity.data.ActivityData;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.entity.data.ProfileData;
import com.pfplaybackend.api.user.domain.enums.ActivityType;
import com.pfplaybackend.api.user.domain.event.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberSignService {
    private final OAuth2RedirectPort oauth2RedirectPort;
    private final MemberRepository memberRepository;
    private final UserProfileCommandService userProfileCommandService;
    private final UserActivityCommandService userActivityCommandService;
    private final PlaylistSetupPort playlistSetupPort;
    private final ApplicationEventPublisher eventPublisher;

    public String getOAuth2RedirectUri(SignMemberCommand command, String redirectLocation) {
        return oauth2RedirectPort.getRedirectUri(command.oauth2Provider(), redirectLocation);
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
        ProfileData profile = userProfileCommandService.createProfileDataForMember(member.getUserId());
        Map<ActivityType, ActivityData> activityMap = userActivityCommandService.createUserActivities(member.getUserId());
        member.initializeProfile(profile);
        member.initializeActivityMap(activityMap);

        playlistSetupPort.createDefaultPlaylist(member.getUserId());

        MemberData saved = memberRepository.save(member);
        eventPublisher.publishEvent(new MemberRegisteredEvent(saved.getUserId(), saved.getEmail(), providerType));
        return saved;
    }
}
