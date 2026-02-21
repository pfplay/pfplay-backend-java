package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackReactionHistoryRepository;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaybackReactionQueryService {

    private final PlaybackReactionHistoryRepository playbackReactionHistoryRepository;

    public Optional<PlaybackReactionHistoryData> findPrevHistoryData(PlaybackId playbackId, UserId userId) {
        return playbackReactionHistoryRepository.findByPlaybackIdAndUserId(playbackId, userId);
    }
}
