package com.pfplaybackend.api.party.adapter.out.persistence;

import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.partyroom.PartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.port.out.PartyroomQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.*;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PartyroomAggregateAdapter implements PartyroomAggregatePort, PartyroomQueryPort {

    private final PartyroomRepository partyroomRepository;
    private final CrewRepository crewRepository;
    private final DjRepository djRepository;
    private final PartyroomPlaybackRepository partyroomPlaybackRepository;
    private final DjQueueRepository djQueueRepository;

    // ===== Root: PartyroomData =====

    @Override
    public Optional<PartyroomData> findPartyroomById(Long id) {
        return partyroomRepository.findById(id);
    }

    @Override
    public PartyroomData savePartyroom(PartyroomData partyroom) {
        return partyroomRepository.save(partyroom);
    }

    @Override
    public Optional<PartyroomData> findByLinkDomain(LinkDomain linkDomain) {
        return partyroomRepository.findByLinkDomain(linkDomain);
    }

    @Override
    public Optional<PartyroomData> findActiveHostRoom(UserId userId) {
        return partyroomRepository.findActiveHostRoom(userId);
    }

    @Override
    public List<PartyroomData> findAllUnusedPartyroomDataByDay(int days) {
        return partyroomRepository.findAllUnusedPartyroomDataByDay(days);
    }

    // ===== Crew: CrewData =====

    @Override
    public Optional<CrewData> findCrew(Long partyroomId, UserId userId) {
        return crewRepository.findByPartyroomDataIdAndUserId(partyroomId, userId);
    }

    @Override
    public Optional<CrewData> findCrewById(Long crewId) {
        return crewRepository.findById(crewId);
    }

    @Override
    public List<CrewData> findCrewsByIds(Iterable<Long> crewIds) {
        return crewRepository.findAllById(crewIds);
    }

    @Override
    public CrewData saveCrew(CrewData crew) {
        return crewRepository.save(crew);
    }

    @Override
    public long countActiveCrews(Long partyroomId) {
        return crewRepository.countByPartyroomDataIdAndIsActiveTrue(partyroomId);
    }

    // ===== DJ: DjData =====

    @Override
    public List<DjData> findDjsOrdered(Long partyroomId) {
        return djRepository.findByPartyroomDataIdOrderByOrderNumberAsc(partyroomId);
    }

    @Override
    public Optional<DjData> findDjById(Long djId) {
        return djRepository.findById(djId);
    }

    @Override
    public Optional<DjData> findDj(Long partyroomId, CrewId crewId) {
        return djRepository.findByPartyroomDataIdAndCrewId(partyroomId, crewId);
    }

    @Override
    public boolean hasDjs(Long partyroomId) {
        return djRepository.existsByPartyroomDataId(partyroomId);
    }

    @Override
    public boolean isDjRegistered(Long partyroomId, CrewId crewId) {
        return djRepository.existsByPartyroomDataIdAndCrewId(partyroomId, crewId);
    }

    @Override
    public DjData saveDj(DjData dj) {
        return djRepository.save(dj);
    }

    @Override
    public void saveDjs(List<DjData> djs) {
        djRepository.saveAll(djs);
    }

    @Override
    public void removeDjs(List<DjData> djs) {
        djRepository.deleteAll(djs);
    }

    // ===== Playback State: PartyroomPlaybackData =====

    @Override
    public PartyroomPlaybackData findPlaybackState(Long partyroomId) {
        return partyroomPlaybackRepository.findById(partyroomId).orElseThrow();
    }

    @Override
    public void savePlaybackState(PartyroomPlaybackData state) {
        partyroomPlaybackRepository.save(state);
    }

    // ===== DJ Queue State: DjQueueData =====

    @Override
    public DjQueueData findDjQueueState(Long partyroomId) {
        return djQueueRepository.findById(partyroomId).orElseThrow();
    }

    @Override
    public void saveDjQueueState(DjQueueData djQueue) {
        djQueueRepository.save(djQueue);
    }

    // ===== Query Port (DTO-returning QueryDSL methods) =====

    @Override
    public Optional<ActivePartyroomDto> getActivePartyroomByUserId(UserId userId) {
        return partyroomRepository.getActivePartyroomByUserId(userId);
    }

    @Override
    public List<PartyroomWithCrewDto> getCrewDataByPartyroomId() {
        return partyroomRepository.getCrewDataByPartyroomId();
    }

    @Override
    public List<PlaybackData> getRecentPlaybackHistory(PartyroomId partyroomId) {
        return partyroomRepository.getRecentPlaybackHistory(partyroomId);
    }
}
