package com.pfplaybackend.api.user.service;

import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.config.TokenProvider;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.common.enums.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService {
    private final UserService userService;
    private final TokenProvider tokenProvider;

    public JwtTokenInfo getUserDetails(Authentication authentication) {
        JwtTokenInfo jwtTokenInfo = JwtTokenInfo.builder()
                .authentication(authentication)
                .build();

        // @TODO JWT refresh token 로직 추가 필요
        if(jwtTokenInfo.isGuest()) {
            return JwtTokenInfo.builder()
                    .authentication(authentication)
                    .user(null)
                    .build();
        }

        User user = Optional.ofNullable(userService.findByUser(jwtTokenInfo.getEmail()))
                .orElseThrow(() -> new NoSuchElementException(ExceptionEnum.NO_SUCH_ELEMENT.getMessage()));

        return JwtTokenInfo.builder()
                .authentication(authentication)
                .user(user)
                .build();
    }
}