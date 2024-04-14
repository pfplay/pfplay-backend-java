package com.pfplaybackend.api.config.oauth2.dto;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomAuthentication extends UsernamePasswordAuthenticationToken {

    private final String email;

    public CustomAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String email) {
        super(principal, credentials, authorities);
        this.email = email;
    }
}
