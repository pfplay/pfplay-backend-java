package com.pfplaybackend.api.security.handle;

import com.pfplaybackend.api.config.TokenProvider;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.enums.Authority;
import com.pfplaybackend.api.enums.Header;
import com.pfplaybackend.api.user.dto.UserSaveDto;
import com.pfplaybackend.api.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    final String REDIRECT_CLIENT_URL = "https://pfplay.io";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        log.info("auth authentication={}", authentication);
//        log.info("auth getAuthorities={}", authentication.getAuthorities());
//        log.info("auth getPrincipal={}", authentication.getPrincipal().toString());
//        log.info("===in");
        log.info("===response={}", request.getParameter("hd"));
        String email = "";
        if(Objects.isNull(request.getParameter("hd"))) {
            email = request.getParameter("hd") + "@gmail.com";
        }

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

        response.addHeader(Header.AUTHORIZATION.getValue(), Header.BEARER.getValue() + accessToken);

//        response.sendRedirect( "/api/v1/user/join");
    }
}
