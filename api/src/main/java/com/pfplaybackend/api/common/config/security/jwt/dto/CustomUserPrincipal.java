package com.pfplaybackend.api.common.config.security.jwt.dto;

import com.pfplaybackend.api.common.config.security.enums.AccessLevel;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CustomUserPrincipal implements OAuth2User, UserDetails {

    private final String name;
    private final UserId userId;
    private final Collection<GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public static CustomUserPrincipal create(UserId userId, AccessLevel accessLevel) {
        return new CustomUserPrincipal(
                userId.getUid().toString(),
                userId,
                Collections.singletonList(new SimpleGrantedAuthority(accessLevel.toString())
        ));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
