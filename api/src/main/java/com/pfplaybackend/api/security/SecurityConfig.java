package com.pfplaybackend.api.security;

import com.pfplaybackend.api.security.service.PrincipalOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOAuth2UserService principalOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/login").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .httpBasic();

        http.oauth2Login()
                .userInfoEndpoint()
                .userService(principalOAuth2UserService);

        return http.build();
    }
}
