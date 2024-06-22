package com.pfplaybackend.api.playlist.domain.service;

import org.springframework.stereotype.Service;

@Service
public class PlaylistDomainService {

    /**
     * 생성할 수 있는 개수를 초과했는가?
     * @return
     */
    public boolean isExceedNumberToCreate() {
        return false;
    }
}