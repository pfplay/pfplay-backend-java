package com.pfplaybackend.api.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.pfplaybackend.api.config.jwt.enums.TokenClaim;
import com.pfplaybackend.api.config.jwt.enums.TokenSubject;
import com.pfplaybackend.api.config.oauth2.dto.CustomUserPrincipal;
import com.pfplaybackend.api.config.oauth2.enums.AccessLevel;
import com.pfplaybackend.api.user.model.domain.MemberDomain;
import com.pfplaybackend.api.user.model.enums.AuthorityTier;
import com.pfplaybackend.api.user.model.value.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    /**
     * AccessToken 생성 메소드
     */
    public String generateAccessTokenForMember(CustomUserPrincipal customUserPrincipal) {
        MemberDomain memberDomain = (MemberDomain) customUserPrincipal.getUserDomain();
        Date now = new Date();
        return JWT.create()
                .withSubject(TokenSubject.ACCESS_TOKEN_SUBJECT.getValue())
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(TokenClaim.UID.getValue(), memberDomain.getUserId().getUid().toString())
                .withClaim(TokenClaim.EMAIL.getValue(), memberDomain.getEmail())
                .withClaim(TokenClaim.ACCESS_LEVEL.getValue(), AccessLevel.ROLE_MEMBER.toString())
                .withClaim(TokenClaim.AUTHORITY_TIER.getValue(), memberDomain.getAuthorityTier().toString())
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String generateAccessTokenForGuest(UserId uid) {
        Date now = new Date();
        return JWT.create()
                .withSubject(TokenSubject.ACCESS_TOKEN_SUBJECT.getValue())
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(TokenClaim.UID.getValue(), uid.getUid().toString())
                .withClaim(TokenClaim.EMAIL.getValue(), "N/A")
                .withClaim(TokenClaim.ACCESS_LEVEL.getValue(), AccessLevel.ROLE_GUEST.toString())
                .withClaim(TokenClaim.AUTHORITY_TIER.getValue(), AuthorityTier.GT.toString())
                .sign(Algorithm.HMAC512(secretKey));
    }
}