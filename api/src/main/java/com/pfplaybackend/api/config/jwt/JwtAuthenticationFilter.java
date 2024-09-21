package com.pfplaybackend.api.config.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.pfplaybackend.api.config.jwt.enums.TokenClaim;
import com.pfplaybackend.api.config.jwt.handler.JwtAuthenticationFailureHandler;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mortbay.log.Log;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Set<String> skipableURIs = new HashSet<>(Set.of(
            "/ws",
            "/error",
            "/error/**",
            "/v3/api-docs",
            "/spec/swagger-ui",
            "/swagger-ui",
            "/api/v1/users/members/sign",
            "/api/v1/users/guests/sign"
    ));
    private final JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler = new JwtAuthenticationFailureHandler();
    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if(isNotSkipableURI(request.getRequestURI())) {
            try {
                final String accessToken = jwtValidator.extractAccessTokenFromCookie(request).orElseThrow(() -> new AuthenticationServiceException("Token does not exist"));
                if(jwtValidator.isTokenValid(accessToken)) {
                    checkAccessTokenAndAuthentication(accessToken);
                }else {
                    // TODO 401 리턴일 때, '토큰 만료'인지 '토큰 부재(또는 부적합)'인지 여부를 판단할 수 있어야 한다.
                    throw new AuthenticationServiceException("Token is not Valid");
                }
            }catch (AuthenticationException e) {
                Log.debug(e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isNotSkipableURI(String requestURI) {
        return skipableURIs.stream().noneMatch(requestURI::startsWith);
    }

    private void checkAccessTokenAndAuthentication(String accessToken) throws ServletException, IOException {
        saveAuthentication(jwtValidator.getDecodedJWT(accessToken));
    }

    private void saveAuthentication(DecodedJWT decodedJWT) {
        UserCredentials customUserPrincipal = jwtValidator.getCustomUserPrincipal(decodedJWT);
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(decodedJWT.getClaim(TokenClaim.ACCESS_LEVEL.getValue()).toString()));
        CustomAuthentication authentication = new CustomAuthentication(customUserPrincipal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
