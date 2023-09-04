package com.pfplaybackend.api.common;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

@Getter
public class JwtTokenInfo {

    private final JwtAuthenticationToken token;
    private final Collection<GrantedAuthority> authorities;
    private final String email;
    private final Long userId;

    public JwtTokenInfo(Authentication authentication) {
        this.token = (JwtAuthenticationToken) authentication;
        this.email = token.getToken().getClaims().get("iss").toString();
        this.userId = Long.parseLong(token.getToken().getClaims().get("userId").toString());
        this.authorities = token.getAuthorities();
    }
}
