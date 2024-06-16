package com.pfplaybackend.api.config.security;

import com.pfplaybackend.api.config.jwt.JwtAuthenticationEntryPoint;
import com.pfplaybackend.api.config.jwt.JwtAuthenticationFilter;
import com.pfplaybackend.api.config.jwt.JwtProvider;
import com.pfplaybackend.api.config.jwt.JwtValidator;
import com.pfplaybackend.api.config.jwt.handler.JwtAuthenticationFailureHandler;
import com.pfplaybackend.api.config.oauth2.CustomOAuth2AuthorizationRequestResolver;
import com.pfplaybackend.api.config.oauth2.handler.OAuth2LoginFailureHandler;
import com.pfplaybackend.api.config.oauth2.handler.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // RedirectUri after 'social login'
    @Value("${app.redirect.web.uri}")
    private String redirectWebUri;

    // OAuth2
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    // Jwt
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headerConfig -> headerConfig
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/error",
                                "/v3/api-docs/**",
                                "/spec/swagger-ui/**",
                                "/swagger-ui/**",
                                "/api/v1/members/sign",
                                "/api/v1/guests/sign").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(customOAuth2AuthorizationRequestResolver())
                        )
                        .userInfoEndpoint(infoEndpoint -> infoEndpoint
                                .userService(oAuth2UserService)
                        )
                        .successHandler(oauth2LoginSuccessHandler())
                        .failureHandler(oauth2LoginFaHandler())
                )
                .addFilterBefore(jwtAuthenticationFilter(), BasicAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );
        return http.build();
    }

    private OAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver() {
        return new CustomOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtValidator);
    }

    private AuthenticationSuccessHandler oauth2LoginSuccessHandler() {
        return new OAuth2LoginSuccessHandler(redirectWebUri, jwtProvider);
    }

    private AuthenticationFailureHandler oauth2LoginFaHandler() {
        return new OAuth2LoginFailureHandler();
    }
}
