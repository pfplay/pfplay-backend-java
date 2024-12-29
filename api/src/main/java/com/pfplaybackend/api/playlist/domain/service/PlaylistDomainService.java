package com.pfplaybackend.api.playlist.domain.service;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.playlist.exception.PlaylistException;
import com.pfplaybackend.api.playlist.exception.TrackException;
import org.springframework.stereotype.Service;

@Service
public class PlaylistDomainService {


    /**
     *
     * @param authorityTier
     * @param size
     */
    public void checkWhetherExceedConstraints(AuthorityTier authorityTier, int size) {
        if(authorityTier.equals(AuthorityTier.FM) && size >= 10) throw ExceptionCreator.create(PlaylistException.EXCEEDED_PLAYLIST_LIMIT);
        if(authorityTier.equals(AuthorityTier.AM) && size >= 1) throw ExceptionCreator.create(PlaylistException.NO_WALLET);
    }
}