package com.pfplaybackend.api.partyroom.application.proxy;

import com.pfplaybackend.api.partyroom.application.dto.MusicDto;
import com.pfplaybackend.api.partyroom.application.peer.MusicQueryPeerService;
import com.pfplaybackend.api.partyroom.domain.value.PlaylistId;
import com.pfplaybackend.api.playlist.application.service.MusicQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusicQueryProxyService implements MusicQueryPeerService {
    private final MusicQueryService musicQueryService;

    @Override
    public MusicDto getFirstMusic(PlaylistId playlistId) {
        return musicQueryService.getFirstMusic(playlistId.getId());
    }
}