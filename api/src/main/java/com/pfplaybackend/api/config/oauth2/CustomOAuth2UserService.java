package com.pfplaybackend.api.config.oauth2;


import com.pfplaybackend.api.config.oauth2.dto.Oauth2UserInfoDto;
import com.pfplaybackend.api.config.oauth2.dto.UserPrincipal;
import com.pfplaybackend.api.config.oauth2.enums.ProviderType;
import com.pfplaybackend.api.user.enums.UserTier;
import com.pfplaybackend.api.user.model.entity.user.User;
import com.pfplaybackend.api.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @SneakyThrows
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        // OAuth2AccessToken accessToken = oAuth2UserRequest.getAccessToken();
        return processOAuth2User(oAuth2UserRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        // TODO OAuth2User.getAttributes() 에는 사전에 합의된 사용자 리소스 정보가 key:value 로 이루어져 있다.
        Oauth2UserInfoDto userInfoDto = Oauth2UserInfoDto
                .builder()
                .email(oAuth2User.getAttributes().get("email").toString())
                .build();

        log.trace("User info is {}", userInfoDto);
        Optional<User> userOptional = userRepository.findByEmail(userInfoDto.getEmail());
        log.trace("User is {}", userOptional);
        User user = userOptional
                .map(existingUser -> updateExistingUser(existingUser, userInfoDto))
                .orElseGet(() -> registerNewUser(oAuth2UserRequest, userInfoDto));
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }
    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, Oauth2UserInfoDto userInfoDto) {
        // TODO name 은 고유 식별자로서 동작해야한다.
        User user = User.builder()
                .name(userInfoDto.getEmail())
                .email(userInfoDto.getEmail())
                .userTier(UserTier.AM)
                .providerType(ProviderType.GOOGLE)
                .build();
        // user.setId(UUID.randomUUID());
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, Oauth2UserInfoDto userInfoDto) {
        return userRepository.save(existingUser);
    }
}
