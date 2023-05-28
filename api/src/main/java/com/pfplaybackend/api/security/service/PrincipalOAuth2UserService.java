package com.pfplaybackend.api.security.service;

import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = Objects.requireNonNull(oAuth2User.getAttribute("email")).toString();
        User user = saveOrUpdate(email);
        return PrincipalDetails.create(user);
    }

    private User saveOrUpdate(String email) {
        User user = userRepository.findById(email).orElse(new User(email));
        return userRepository.save(user);
    }
}
