package com.pfplaybackend.api.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.pfplaybackend.api.config.jwt.enums.TokenClaim;
import com.pfplaybackend.api.config.jwt.enums.TokenSubject;
import com.pfplaybackend.api.config.oauth2.dto.CustomUserPrincipal;
import com.pfplaybackend.api.config.security.enums.AccessLevel;
import com.pfplaybackend.api.user.domain.model.domain.Guest;
import com.pfplaybackend.api.user.domain.model.domain.Member;
import com.pfplaybackend.api.user.domain.model.enums.AuthorityTier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
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
        Member member = (Member) customUserPrincipal.getUser();
        Date now = new Date();
        return JWT.create()
                .withSubject(TokenSubject.ACCESS_TOKEN_SUBJECT.getValue())
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(TokenClaim.UID.getValue(), member.getUserId().getUid().toString())
                .withClaim(TokenClaim.EMAIL.getValue(), member.getEmail())
                .withClaim(TokenClaim.ACCESS_LEVEL.getValue(), AccessLevel.ROLE_MEMBER.toString())
                .withClaim(TokenClaim.AUTHORITY_TIER.getValue(), member.getAuthorityTier().toString())
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String generateAccessTokenForMember(Member member) {
        Date now = new Date();
        return JWT.create()
                .withSubject(TokenSubject.ACCESS_TOKEN_SUBJECT.getValue())
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(TokenClaim.UID.getValue(), member.getUserId().getUid().toString())
                .withClaim(TokenClaim.EMAIL.getValue(), member.getEmail())
                .withClaim(TokenClaim.ACCESS_LEVEL.getValue(), AccessLevel.ROLE_MEMBER.toString())
                .withClaim(TokenClaim.AUTHORITY_TIER.getValue(), member.getAuthorityTier().toString())
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String generateAccessTokenForGuest(Guest guest) {
        Date now = new Date();
        return JWT.create()
                .withSubject(TokenSubject.ACCESS_TOKEN_SUBJECT.getValue())
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(TokenClaim.UID.getValue(), guest.getUserId().getUid().toString())
                .withClaim(TokenClaim.EMAIL.getValue(), "N/A")
                .withClaim(TokenClaim.ACCESS_LEVEL.getValue(), AccessLevel.ROLE_GUEST.toString())
                .withClaim(TokenClaim.AUTHORITY_TIER.getValue(), AuthorityTier.GT.toString())
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String generateNonExpiringAccessTokenForGuest(Guest guest) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2099, Calendar.DECEMBER, 31, 23, 59, 59);
        Date specificDate = calendar.getTime();
        return JWT.create()
                .withSubject(TokenSubject.ACCESS_TOKEN_SUBJECT.getValue())
                .withExpiresAt(specificDate)
                .withClaim(TokenClaim.UID.getValue(), guest.getUserId().getUid().toString())
                .withClaim(TokenClaim.EMAIL.getValue(), "N/A")
                .withClaim(TokenClaim.ACCESS_LEVEL.getValue(), AccessLevel.ROLE_GUEST.toString())
                .withClaim(TokenClaim.AUTHORITY_TIER.getValue(), AuthorityTier.GT.toString())
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String generateNonExpiringAccessTokenForMember(Member member) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2099, Calendar.DECEMBER, 31, 23, 59, 59);
        Date specificDate = calendar.getTime();
        return JWT.create()
                .withSubject(TokenSubject.ACCESS_TOKEN_SUBJECT.getValue())
                .withExpiresAt(specificDate)
                .withClaim(TokenClaim.UID.getValue(), member.getUserId().getUid().toString())
                .withClaim(TokenClaim.EMAIL.getValue(), member.getEmail())
                .withClaim(TokenClaim.ACCESS_LEVEL.getValue(), AccessLevel.ROLE_MEMBER.toString())
                .withClaim(TokenClaim.AUTHORITY_TIER.getValue(), member.getAuthorityTier().toString())
                .sign(Algorithm.HMAC512(secretKey));
    }
}