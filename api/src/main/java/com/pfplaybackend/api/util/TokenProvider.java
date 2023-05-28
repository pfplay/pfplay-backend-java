package com.pfplaybackend.api.util;

import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.security.service.PrincipalDetails;
import com.pfplaybackend.api.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private Key tokenSecretKey;
    private long tokenExpirationMs;
    private long refreshTokenExpirationMs;
    private final String AUTHORITIES_KEY = "role";

    private final UserRepository userRepository;

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(tokenSecretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token");
        } catch (Exception e) {
            log.warn("Invalid JWT token");
        }
        return false;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenSecretKey)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String getAccessToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getAuthority().getRole())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(tokenExpirationMs)))
                .signWith(SignatureAlgorithm.HS512, tokenSecretKey)
                .compact();
    }

    public String getRefreshToken() {
        Instant now = Instant.now();

        return Jwts.builder()
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(refreshTokenExpirationMs)))
                .signWith(SignatureAlgorithm.HS512, tokenSecretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Map<String, Object> claims = getClaims(token);
        String subject = getSubject(claims);
        String role = getRole(claims);

        PrincipalDetails userDetails = PrincipalDetails.create(subject, role);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Map<String, Object> getClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(tokenSecretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String getSubject(Map<String, Object> claims) {
        return claims.get("sub").toString();
    }

    public String getRole(Map<String, Object> claims) {
        return claims.get("role").toString();
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (!StringUtils.isBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
