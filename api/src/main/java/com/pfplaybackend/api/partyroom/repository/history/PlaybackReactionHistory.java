package com.pfplaybackend.api.partyroom.repository.history;


import com.pfplaybackend.api.partyroom.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.partyroom.domain.value.PlaybackId;
import com.pfplaybackend.api.user.domain.value.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackReactionHistory extends JpaRepository<PlaybackReactionHistoryData, Long> {
    PlaybackReactionHistoryData findByPlaybackIdAndUserId(PlaybackId playbackId, UserId userId);
}
