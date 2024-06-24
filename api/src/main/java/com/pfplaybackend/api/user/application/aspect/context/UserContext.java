package com.pfplaybackend.api.user.application.aspect.context;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserContext {
    UserId userId;
    AuthorityTier authorityTier;

    public static UserContext create(UserCredentials userCredentials) {
        return new UserContext(new UserId(userCredentials.getUid()), userCredentials.getAuthorityTier());
    }
}
