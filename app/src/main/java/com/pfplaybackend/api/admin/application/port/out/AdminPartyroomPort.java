package com.pfplaybackend.api.admin.application.port.out;

import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.DjQueueData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackAggregationData;
import com.pfplaybackend.api.party.domain.entity.data.history.PlaybackReactionHistoryData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;

import java.util.List;
import java.util.Optional;

public interface AdminPartyroomPort {

    // Partyroom
    List<PartyroomData> findAllPartyrooms();
    Optional<PartyroomData> findPartyroomById(Long id);
    Optional<PartyroomData> findPartyroomByLinkDomain(LinkDomain linkDomain);
    PartyroomData savePartyroom(PartyroomData partyroom);

    // Playback State
    Optional<PartyroomPlaybackData> findPlaybackState(PartyroomId partyroomId);
    PartyroomPlaybackData savePlaybackState(PartyroomPlaybackData playbackState);

    // DJ Queue
    DjQueueData saveDjQueue(DjQueueData djQueue);

    // Crew
    CrewData saveCrew(CrewData crew);
    Optional<CrewData> findCrewByPartyroomAndUser(PartyroomId partyroomId, UserId userId);
    List<CrewData> findActiveCrewByPartyroom(PartyroomId partyroomId);
    long countActiveCrewByPartyroom(PartyroomId partyroomId);

    // DJ
    DjData saveDj(DjData dj);
    boolean existsDjByPartyroomAndCrew(PartyroomId partyroomId, CrewId crewId);
    List<DjData> findDjsByPartyroomOrderByOrder(PartyroomId partyroomId);

    // Playback Aggregation
    Optional<PlaybackAggregationData> findPlaybackAggregation(PlaybackId playbackId);

    // Playback Reaction History
    Optional<PlaybackReactionHistoryData> findReactionHistory(PlaybackId playbackId, UserId userId);
    PlaybackReactionHistoryData saveReactionHistory(PlaybackReactionHistoryData history);
}
