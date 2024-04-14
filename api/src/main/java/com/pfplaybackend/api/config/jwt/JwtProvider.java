package com.pfplaybackend.api.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.pfplaybackend.api.config.oauth2.enums.AuthorityType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String USER_ID = "userid";
    private static final String EMAIL_CLAIM = "email";
    private static final String AUTHORITY_CLAIM = "authority";

    /**
     * AccessToken 생성 메소드
     */
    public String generateAccessTokenForMember(String email) {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(EMAIL_CLAIM, email)
                .withClaim(AUTHORITY_CLAIM, AuthorityType.ROLE_MEMBER.toString())
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String generateAccessTokenForGuest(Long Id) {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(USER_ID, Id)
                .withClaim(EMAIL_CLAIM, "empty")
                .withClaim(AUTHORITY_CLAIM, AuthorityType.ROLE_GUEST.toString())
                .sign(Algorithm.HMAC512(secretKey));
    }
}