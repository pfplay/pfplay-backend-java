package com.pfplaybackend.api.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jwt.JWT;
import com.pfplaybackend.api.common.ApiResponse;
import com.pfplaybackend.api.common.ResponseMessage;
import com.pfplaybackend.api.config.ObjectMapperConfig;
import com.pfplaybackend.api.config.TokenProvider;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.enums.Header;
import com.pfplaybackend.api.user.dto.DummyResponse;
import com.pfplaybackend.api.user.dto.UserSaveDto;
import com.pfplaybackend.api.user.service.UserService;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UserSignController {

    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final ObjectMapperConfig ob;

    final String REDIRECT_CLIENT_URL = "https://pfplay.io";

    @GetMapping("/login")
    public void join(HttpServletResponse response, HttpServletRequest request) throws IOException {
        // 로그인 페이지로 리다이렉트
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/join")
    public void join(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) throws IOException {
        String email = oAuth2User.getAttributes().get("email").toString();
        Optional<User> findUser = Optional.ofNullable(userService.findByEmail(email));

        String accessToken = "";

        if (findUser.isEmpty()) {
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

//        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
//                .httpOnly(true)
//                .secure(true) // HTTPS를 사용하는 경우에만 secure로 설정
//                .path("/") // 쿠키의 유효 범위 설정
//                .build();
//
//        // 응답 헤더에 쿠키 추가
//        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader(Header.AUTHORIZATION.getValue(), Header.BEARER.getValue() + accessToken);
        response.setHeader("Location", REDIRECT_CLIENT_URL);
        response.setStatus(HttpServletResponse.SC_FOUND);

//        return ResponseEntity.ok().body(ApiResponse.success(ResponseMessage.make(HttpStatus.OK.value(), HttpStatus.OK.name())));
    }

    @GetMapping("/dummy")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> dummy() {
        // JwtAuthenticationToken
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(new DummyResponse(jwtAuthenticationToken));
    }

    @GetMapping("/jwt")
    @PreAuthorize("hasRole('USER_ADMIN')")
    public ResponseEntity<?> jwt() {
        return ResponseEntity.ok().build();
    }

}
