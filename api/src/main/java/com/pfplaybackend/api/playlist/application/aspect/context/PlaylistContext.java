package com.pfplaybackend.api.playlist.application.aspect.context;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.config.jwt.dto.UserCredentials;
import com.pfplaybackend.api.user.application.aspect.context.UserContext;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaylistContext {
    UserId userId;
    AuthorityTier authorityTier;

    public static PlaylistContext create(UserCredentials userCredentials) {
        return new PlaylistContext(new UserId(userCredentials.getUid()), userCredentials.getAuthorityTier());
    }
}
