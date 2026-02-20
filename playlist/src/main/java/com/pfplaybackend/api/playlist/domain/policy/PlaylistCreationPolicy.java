package com.pfplaybackend.api.playlist.domain.policy;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.playlist.domain.exception.PlaylistException;

public class PlaylistCreationPolicy {

    private static final int FM_MAX = 10;
    private static final int AM_MAX = 1;

    public void enforce(AuthorityTier tier, int currentCount) {
        int limit = (tier == AuthorityTier.FM) ? FM_MAX : AM_MAX;
        if (currentCount >= limit) throw ExceptionCreator.create(PlaylistException.EXCEEDED_PLAYLIST_LIMIT);
    }
}
