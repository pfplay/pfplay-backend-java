package com.pfplaybackend.api.partyroom.application.aspect.context;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.playlist.application.aspect.context.PlaylistContext;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.Authentication;

@Getter
@AllArgsConstructor
public class PartyContext {
    UserId userId;
    AuthorityTier authorityTier;

    public static PartyContext create(UserCredentials userCredentials) {
        return new PartyContext(new UserId(userCredentials.getUid()), userCredentials.getAuthorityTier());
    }
}
