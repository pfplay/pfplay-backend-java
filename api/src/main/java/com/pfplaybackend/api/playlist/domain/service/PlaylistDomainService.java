package com.pfplaybackend.api.playlist.domain.service;

import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.playlist.exception.PlaylistLimitExceededException;
import com.pfplaybackend.api.playlist.exception.PlaylistNoWalletException;
import org.springframework.stereotype.Service;

@Service
public class PlaylistDomainService {


    /**
     *
     * @param authorityTier
     * @param size
     */
    public void checkWhetherExceedConstraints(AuthorityTier authorityTier, int size) {
        if(authorityTier.equals(AuthorityTier.FM) && size >= 10) throw new PlaylistLimitExceededException("생성 개수 제한 초과");
        if(authorityTier.equals(AuthorityTier.AM) && size >= 1) throw new PlaylistNoWalletException("생성 개수 제한 초과 (지갑 미연동)");
    }
}