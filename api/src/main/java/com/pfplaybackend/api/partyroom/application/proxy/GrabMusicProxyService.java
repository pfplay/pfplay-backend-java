package com.pfplaybackend.api.partyroom.application.proxy;

import com.pfplaybackend.api.partyroom.application.peer.GrabMusicPeerService;
import com.pfplaybackend.api.playlist.application.service.GrabMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrabMusicProxyService implements GrabMusicPeerService {

    private final GrabMusicService grabMusicService;

    @Override
    public void grabMusic(String linkId) {
        grabMusicService.grabMusic();
    }
}
