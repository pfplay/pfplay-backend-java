package com.pfplaybackend.api.partyroom.repository.history;


import com.pfplaybackend.api.partyroom.domain.entity.data.history.PlaybackReactionHistoryData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackReactionHistory extends JpaRepository<PlaybackReactionHistoryData, Long> {
}
