package com.pfplaybackend.api.common.config.security.jwt;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {

    private final UserId userId;
    private final String email;
    private final AuthorityTier authorityTier;
    private final String provider;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomJwtAuthenticationToken that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(userId, that.userId)
                && Objects.equals(email, that.email)
                && authorityTier == that.authorityTier
                && Objects.equals(provider, that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, email, authorityTier, provider);
    }
}