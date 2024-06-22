package com.pfplaybackend.api.config.oauth2.handler;

import com.pfplaybackend.api.config.jwt.JwtProvider;
import com.pfplaybackend.api.config.jwt.util.CookieUtil;
import com.pfplaybackend.api.config.oauth2.dto.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final String redirectWebUri;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // Attach 'access token' to cookies
        attachAccessTokenToCookie(authentication, response);
        // Redirect where the user wants to go
        redirectToView(response, extractRedirectLocationFromQueryString(request));
    }

    private void attachAccessTokenToCookie(Authentication authentication, HttpServletResponse response) {
        // Generate 'access token' for 'social login' user
        String accessToken = jwtProvider.generateAccessTokenForMember((CustomUserPrincipal) authentication.getPrincipal());
        ResponseCookie responseCookie = CookieUtil.getCookieWithToken("AccessToken", accessToken);
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    private void redirectToView(HttpServletResponse response, String redirectLocation) throws IOException {
        // TODO
        System.out.println(this.redirectWebUri + "/" + redirectLocation);
        response.sendRedirect(this.redirectWebUri + redirectLocation);
        // response.sendRedirect(this.redirectWebUri + "/index.html");
    }

    private String extractRedirectLocationFromQueryString(HttpServletRequest request) {
        return request.getParameterMap().get("state")[0];
    }
}
