package com.pfplaybackend.api.config.oauth2;


import com.pfplaybackend.api.config.oauth2.dto.CustomUserPrincipal;
import com.pfplaybackend.api.user.application.MemberSignService;
import com.pfplaybackend.api.user.model.domain.MemberDomain;
import com.pfplaybackend.api.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final MemberSignService memberSignService;

    @Override
    @SneakyThrows
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        return processOAuth2User(oAuth2UserRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        // OAuth2User.getAttributes() 에는 사전에 합의된 사용자 리소스 정보가 key:value 로 이루어져 있다.
        MemberDomain memberDomain = memberSignService.getMemberDomain(oAuth2User.getAttributes().get("email").toString());
        return CustomUserPrincipal.create(memberDomain, oAuth2User.getAttributes());
    }
}
