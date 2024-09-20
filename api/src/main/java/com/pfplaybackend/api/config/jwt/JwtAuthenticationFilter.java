package com.pfplaybackend.api.config.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.pfplaybackend.api.common.HttpServletResponseHandler;
import com.pfplaybackend.api.config.jwt.enums.TokenClaim;
import com.pfplaybackend.api.config.jwt.exception.JwtAuthenticationException;
import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

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

    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if(isNotSkipableURI(request.getRequestURI())) {
            Optional<String> accessTokenOptional = jwtValidator.extractAccessTokenFromCookie(request);
            if (accessTokenOptional.isEmpty()) {
                HttpServletResponseHandler.setByException(response, JwtAuthenticationException.ACCESS_TOKEN_NOT_FOUND);
                return;
            }
            final String accessToken = accessTokenOptional.get();

            if (!jwtValidator.isTokenValid(accessToken)) {
                HttpServletResponseHandler.setByException(response, JwtAuthenticationException.ACCESS_TOKEN_INVALID);
                return;
            }
            saveAuthentication(jwtValidator.getDecodedJWT(accessToken));
        }
        filterChain.doFilter(request, response);
    }

    private boolean isNotSkipableURI(String requestURI) {
        return skipableURIs.stream().noneMatch(requestURI::startsWith);
    }

    private void saveAuthentication(DecodedJWT decodedJWT) {
        UserCredentials customUserPrincipal = jwtValidator.getCustomUserPrincipal(decodedJWT);
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(decodedJWT.getClaim(TokenClaim.ACCESS_LEVEL.getValue()).toString()));
        CustomAuthentication authentication = new CustomAuthentication(customUserPrincipal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
