package com.pfplaybackend.api.common.config.security.jwt;

import com.pfplaybackend.api.common.config.security.enums.AccessLevel;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        // JWT에서 '클레임' 정보 추출
        UserId userId = UserId.fromString(jwt.getClaim("uid"));
        String email = jwt.getClaim("email");
        AccessLevel accessLevel = AccessLevel.valueOf(jwt.getClaim("access_level"));
        AuthorityTier authorityTier = AuthorityTier.valueOf(jwt.getClaim("authority_tier"));
        String provider = jwt.getClaim("provider");

        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(accessLevel.name()));
        // Custom Authentication Token 생성
        return new CustomJwtAuthenticationToken(
                jwt,
                authorities,
                userId,
                email,
                authorityTier,
                provider
        );
    }
}
