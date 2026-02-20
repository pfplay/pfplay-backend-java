package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.port.PlaybackStatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaybackStateAdapter implements PlaybackStatePort {

    private final PartyroomPlaybackRepository partyroomPlaybackRepository;

    @Override
    public PartyroomPlaybackData findByPartyroomId(Long partyroomId) {
        return partyroomPlaybackRepository.findById(partyroomId).orElseThrow();
    }

    @Override
    public void save(PartyroomPlaybackData state) {
        partyroomPlaybackRepository.save(state);
    }
}
