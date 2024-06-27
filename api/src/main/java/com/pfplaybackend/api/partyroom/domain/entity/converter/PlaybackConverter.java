package com.pfplaybackend.api.partyroom.domain.entity.converter;

import com.pfplaybackend.api.partyroom.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Playback;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaybackConverter {
    public Playback toDomain(PlaybackData playbackData) {
        return new Playback();
    }

    public PlaybackData toDomain(Playback playback) {
        return new PlaybackData();
    }
}
