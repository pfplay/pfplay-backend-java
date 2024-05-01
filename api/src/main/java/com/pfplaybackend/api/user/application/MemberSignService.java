package com.pfplaybackend.api.user.application;

import com.pfplaybackend.api.config.oauth2.enums.ProviderType;
import com.pfplaybackend.api.config.oauth2.properties.OAuth2ProviderConfig;
import com.pfplaybackend.api.config.oauth2.model.OAuth2Redirection;
import com.pfplaybackend.api.user.model.domain.MemberDomain;
import com.pfplaybackend.api.user.model.entity.Member;
import com.pfplaybackend.api.user.presentation.dto.request.MemberSignRequest;
import com.pfplaybackend.api.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberSignService {

    final private OAuth2ProviderConfig oauth2ProviderConfig;
    private final MemberRepository memberRepository;

    public String getOAuth2RedirectUri(MemberSignRequest request) {
        OAuth2Redirection oauth2Redirection = OAuth2Redirection.create(oauth2ProviderConfig.getProviders(), request.getOauth2Provider(), request.getRedirectLocation());
        return oauth2Redirection.getUrl();
    }

    @Transactional
    public MemberDomain getMemberDomain(String email) {
        return tryFindMember(email).orElseGet(() -> registerNewMember(email)).toDomain();
    }

    private Optional<Member> tryFindMember(String email) {
        return memberRepository.findByEmail(email);
    }

    private Member registerNewMember(String email) {
        // TODO Parse ProviderType
        MemberDomain memberDomain = MemberDomain.create(email, ProviderType.GOOGLE);
        Member member = memberDomain.toEntity();
        return memberRepository.save(member);
    }
}
