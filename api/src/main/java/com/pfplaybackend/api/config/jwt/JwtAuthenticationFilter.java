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

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Set<String> skipableURIs = new HashSet<>(Set.of("/api/v1/members/sign", "/api/v1/guests/sign"));
    private final JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;
    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if(isNotSkipableURI(request.getRequestURI())) {
            try {
                final String accessToken = jwtValidator.extractAccessTokenFromCookie(request).orElseThrow(() -> new AuthenticationServiceException("Token does not exist"));
                if(jwtValidator.isTokenValid(accessToken)) {
                    checkAccessTokenAndAuthentication(accessToken);
                }else {
                    throw new AuthenticationServiceException("Token is not Valid");
                }
            }catch (AuthenticationException  e) {
                jwtAuthenticationFailureHandler.onAuthenticationFailure(request, response, e);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isNotSkipableURI(String requestURI) {
        return !skipableURIs.contains(requestURI);
    }

    private void checkAccessTokenAndAuthentication(String accessToken) throws ServletException, IOException {
        saveAuthentication(jwtValidator.getDecodedJWT(accessToken));
    }

    private void saveAuthentication(DecodedJWT decodedJWT) {
        UserCredentials userAuthentication = jwtValidator.getUserAuthentication(decodedJWT);
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(decodedJWT.getClaim(TokenClaim.ACCESS_LEVEL.getValue()).toString()));
        CustomAuthentication authentication = new CustomAuthentication(userAuthentication, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
