package com.pfplaybackend.api.party.domain.port;

import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;

public interface PlaybackStatePort {
    PartyroomPlaybackData findByPartyroomId(Long partyroomId);
    void save(PartyroomPlaybackData state);
}
