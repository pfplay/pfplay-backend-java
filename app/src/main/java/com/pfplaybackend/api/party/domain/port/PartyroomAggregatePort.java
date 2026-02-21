package com.pfplaybackend.api.party.domain.port;

import com.pfplaybackend.api.party.domain.entity.data.*;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.common.domain.value.UserId;

import java.util.List;
import java.util.Optional;

public interface PartyroomAggregatePort {

    // ===== Root: PartyroomData =====
    Optional<PartyroomData> findPartyroomById(Long id);
    PartyroomData savePartyroom(PartyroomData partyroom);
    Optional<PartyroomData> findByLinkDomain(LinkDomain linkDomain);
    Optional<PartyroomData> findActiveHostRoom(UserId userId);
    List<PartyroomData> findAllUnusedPartyroomDataByDay(int days);

    // ===== Crew: CrewData =====
    Optional<CrewData> findCrew(Long partyroomId, UserId userId);
    Optional<CrewData> findCrewById(Long crewId);
    List<CrewData> findCrewsByIds(Iterable<Long> crewIds);
    CrewData saveCrew(CrewData crew);
    List<CrewData> findActiveCrews(Long partyroomId);
    long countActiveCrews(Long partyroomId);

    // ===== DJ: DjData =====
    List<DjData> findDjsOrdered(Long partyroomId);
    Optional<DjData> findDjById(Long djId);
    Optional<DjData> findDj(Long partyroomId, CrewId crewId);
    boolean hasDjs(Long partyroomId);
    boolean isDjRegistered(Long partyroomId, CrewId crewId);
    DjData saveDj(DjData dj);
    void saveDjs(List<DjData> djs);
    void removeDjs(List<DjData> djs);

    // ===== Playback State: PartyroomPlaybackData =====
    PartyroomPlaybackData findPlaybackState(Long partyroomId);
    void savePlaybackState(PartyroomPlaybackData state);

    // ===== DJ Queue State: DjQueueData =====
    DjQueueData findDjQueueState(Long partyroomId);
    void saveDjQueueState(DjQueueData djQueue);
}
