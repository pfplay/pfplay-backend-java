package com.pfplaybackend.api.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pfplaybackend.api.common.ApiResponse;
import com.pfplaybackend.api.common.ResponseMessage;
import com.pfplaybackend.api.config.TokenProvider;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.enums.Header;
import com.pfplaybackend.api.user.dto.UserSaveDto;
import com.pfplaybackend.api.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserSignController {

    private final TokenProvider tokenProvider;
    private final UserService userService;

    @GetMapping("/login")
    public void join(HttpServletResponse response, HttpServletRequest request) throws IOException {
        // 로그인 페이지로 리다이렉트
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/join")
    public ResponseEntity<?> join(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) throws JsonProcessingException {
        String email = oAuth2User.getAttributes().get("email").toString();
        Optional<User> findUser = Optional.ofNullable(userService.findByEmail(email));

        String accessToken = "";

        if(findUser.isEmpty()) {
            // 회원가입
            UserSaveDto userDto = UserSaveDto.builder()
                    .email(email)
                    .authority(Authority.USER)
                    .build();

            userService.save(userDto.toEntity());
            accessToken = tokenProvider.createAccessToken(userDto.getAuthority(), email);
            log.info("join accessToken={}", accessToken);
        } else {
            accessToken = tokenProvider.createAccessToken(findUser.orElseThrow().getAuthority(), email);
            log.info("join accessToken user={}", accessToken);
        }

        response.addHeader(Header.AUTHORIZATION.getValue(), Header.BEARER.getValue() + accessToken);

        return ResponseEntity.ok().body(ApiResponse.success(ResponseMessage.make(HttpStatus.OK.value(), HttpStatus.OK.name())));
    }

    @GetMapping("/jwt")
    @PreAuthorize("hasRole('USER_ADMIN')")
    public ResponseEntity<?> jwt() {
        return ResponseEntity.ok().build();
    }

}
