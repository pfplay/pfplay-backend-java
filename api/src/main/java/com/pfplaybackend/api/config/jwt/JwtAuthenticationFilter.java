package com.pfplaybackend.api.config.jwt;

import com.pfplaybackend.api.config.oauth2.dto.CustomAuthentication;
import com.pfplaybackend.api.config.oauth2.dto.UserPrincipal;
import com.pfplaybackend.api.user.model.entity.user.User;
import com.pfplaybackend.api.user.repository.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;
    private final UserRepository userRepository;
    private final AuthenticationFailureHandler failureHandler;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String accessToken = jwtValidator.extractAccessTokenFromCookie(request).orElseThrow(() -> new AuthenticationServiceException("Token does not exist"));
            if(jwtValidator.isTokenValid(accessToken)) {
                checkAccessTokenAndAuthentication(request, response, filterChain);
            }else {
                throw new AuthenticationServiceException("Token is not Valid");
            }
        }catch (AuthenticationException  e) {
            failureHandler.onAuthenticationFailure(request, response, e);
        }
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        // System.out.println(jwtValidator.extractAccessTokenFromCookie(request));
        // TODO Member 와 Guest 분기 필요
        jwtValidator.extractAccessTokenFromCookie(request)
                .filter(jwtValidator::isTokenValid)
                .flatMap(jwtValidator::extractEmail)
                .flatMap(userRepository::findByEmail)
                .ifPresent(this::saveAuthentication);
        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(User user) {
        UserDetails userDetailsUser = UserPrincipal.create(user);
        CustomAuthentication authentication = new CustomAuthentication(user.getName(), null, userDetailsUser.getAuthorities(), user.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
