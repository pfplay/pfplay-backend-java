package com.pfplaybackend.api.user.presentation.dto;

import com.pfplaybackend.api.common.JwtTokenInfo;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
@Getter
public class DummyResponse {

    private final String email;
    private final Collection<GrantedAuthority> authorities;

    public DummyResponse(JwtTokenInfo token) {
        this.email = token.getEmail();
        this.authorities = token.getAuthorities();
    }
}
