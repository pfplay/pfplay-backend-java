package com.pfplaybackend.api.user.service;

import com.pfplaybackend.api.common.JwtTokenInfo;
import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.partyroom.presentation.dto.UserDto;
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

    public JwtTokenInfo getUserDetails(Authentication authentication) {
        JwtTokenInfo jwtTokenInfo = JwtTokenInfo.builder()
                .authentication(authentication)
                .build();

        if(jwtTokenInfo.isGuest()) {
            return JwtTokenInfo.builder()
                    .authentication(authentication)
                    .user(null)
                    .build();
        }

        User user = Optional.of(userService.findByUser(jwtTokenInfo.getEmail()))
                .orElseThrow(NoSuchElementException::new);

        return JwtTokenInfo.builder()
                .authentication(authentication)
                .user(user)
                .build();
    }
}