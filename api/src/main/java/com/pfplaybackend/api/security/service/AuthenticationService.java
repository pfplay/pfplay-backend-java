package com.pfplaybackend.api.security.service;

import com.pfplaybackend.api.common.exception.InvalidEmailException;
import com.pfplaybackend.api.common.exception.InvalidTokenException;
import com.pfplaybackend.api.entity.User;
import com.pfplaybackend.api.user.repository.UserRepository;
import com.pfplaybackend.api.util.TokenProvider;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    public User getAccount(String authorizationToken) {
        String userEmail = getEmail(authorizationToken);

        return userRepository.findById(userEmail)
                .orElseThrow(() -> new InvalidEmailException("email is not valid"));
    }

    public String getEmail(String authorizationToken) {
        String token = extractToken(authorizationToken);
        tokenValidationCheck(token);

        return tokenProvider.getEmailFromToken(token);
    }

    private String extractToken(String bearerToken) {
        if (!StringUtils.isBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        throw new InvalidTokenException("token is not bearer type");
    }

    private void tokenValidationCheck(String authorizationToken) {
        if (StringUtils.isBlank(authorizationToken)) {
            throw new InvalidTokenException("token is null");
        }

        if (!tokenProvider.validateToken(authorizationToken)) {
            throw new InvalidTokenException("token is not valid");
        }
    }
}
