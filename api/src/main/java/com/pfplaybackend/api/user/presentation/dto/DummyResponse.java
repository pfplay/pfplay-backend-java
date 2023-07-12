package com.pfplaybackend.api.user.presentation.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
@Getter
public class DummyResponse {

    private final String email;
    private final String testSnakeCase = null;
    private final Collection<GrantedAuthority> authorities;

    public DummyResponse(JwtAuthenticationToken token) {
        this.email = token.getToken().getClaims().get("iss").toString();
        this.authorities = token.getAuthorities();
    }
}
