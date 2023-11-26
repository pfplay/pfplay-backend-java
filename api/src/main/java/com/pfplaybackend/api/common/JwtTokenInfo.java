package com.pfplaybackend.api.common;

import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.enums.Authority;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Objects;

@Getter
@Slf4j
public class JwtTokenInfo implements Authentication {

    private final JwtAuthenticationToken token;
    private final Collection<GrantedAuthority> authorities; // 시큐리티 role
    private final String email;
    private final Long userId;
    private final User user;

    @Builder
    public JwtTokenInfo(final Authentication authentication, final User user) {
        this.token = (JwtAuthenticationToken) authentication;
        this.userId = Long.parseLong(token.getToken().getClaims().get("userId").toString());
        this.email = token.getToken().getClaims().get("iss").toString();
        this.authorities = token.getAuthorities();
        this.user = user;
    }

    public boolean isUser() {
        return !Objects.isNull(user);
    }

    public boolean isWalletUser() {
        return this.user.getAuthority().equals(Authority.ROLE_WALLET_USER);
    }

    public boolean isGuest() {
        return this.authorities.stream().anyMatch(
                o-> o.getAuthority().equals(Authority.ROLE_GUEST.name())
        );
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return this;
    }

    @Override
    public Object getPrincipal() {
        return this.user;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated();
    }

    @Override
    public String getName() {
        return this.user.getNickname();
    }
}
