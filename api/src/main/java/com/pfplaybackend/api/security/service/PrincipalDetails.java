package com.pfplaybackend.api.security.service;

import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.security.AuthorityRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User {

    private final String email;
    private final Collection<GrantedAuthority> authorities;

    public PrincipalDetails(String email) {
        this.email = email;
        this.authorities = List.of(new SimpleGrantedAuthority(AuthorityRole.USER.getRole()));
    }

    public PrincipalDetails(String email, Collection<GrantedAuthority> authorities) {
        this.email = email;
        this.authorities = authorities;
    }

    public static PrincipalDetails create(User user) {
        return new PrincipalDetails(user.getEmail());
    }

    public static PrincipalDetails create(String email, String authority) {
        AuthorityRole authorityRole = AuthorityRole.valueOf(authority);
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authorityRole.getRole()));
        return new PrincipalDetails(email, authorities);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return email;
    }
}
