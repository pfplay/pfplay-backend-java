package com.pfplaybackend.api.common.config.security.jwt;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.user.domain.entity.domainmodel.User;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

@Getter
@Setter
public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {

    private final UserId userId;
    private final String email;
    private final AuthorityTier authorityTier;
    private final String provider;
    private User user;

    public CustomJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities,
                                        UserId userId, String email, AuthorityTier authorityTier, String provider) {
        super(jwt, authorities);
        this.userId = userId;
        this.email = email;
        this.authorityTier = authorityTier;
        this.provider = provider;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}