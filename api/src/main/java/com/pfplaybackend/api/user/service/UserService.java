package com.pfplaybackend.api.user.service;

import com.pfplaybackend.api.config.TokenProvider;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.user.presentation.dto.UserSaveDto;
import com.pfplaybackend.api.user.presentation.request.ProfileUpdateRequest;
import com.pfplaybackend.api.user.repository.UserRepository;
import com.pfplaybackend.api.config.WebClientConfig;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WebClientConfig web;
    private final TokenProvider tokenProvider;

    public UserService(UserRepository userRepository, WebClientConfig web, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.web = web;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public String notRegisteredUserReturnJwt(String email) {
        // 회원가입
        UserSaveDto userDto = UserSaveDto.builder()
                .email(email)
                .authority(Authority.USER)
                .build();

        userRepository.save(userDto.toEntity());
        return tokenProvider.createAccessToken(userDto.getAuthority(), email);
    }

    public String registeredUserReturnJwt(User user, String email) {
        return tokenProvider.createAccessToken(user.getAuthority(), email);
    }

    @Transactional(readOnly = true)
    public User findByUser(String email) {
        return userRepository.findByEmail(email);
    }

    public <T> T request(String uri, Class<T> responseType) {
        return web.webClient()
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    @Transactional
    public void updateProfile(User user, ProfileUpdateRequest request) {
        user.setIntroduction(request.getIntroduction());
        user.setNickname(request.getNickname());
        user.setFaceUrl(request.getFaceUrl());
        user.setBodyId(request.getBodyId());
        user.setWalletAddress(request.getWalletAddress());
        userRepository.save(user);
    }
}
