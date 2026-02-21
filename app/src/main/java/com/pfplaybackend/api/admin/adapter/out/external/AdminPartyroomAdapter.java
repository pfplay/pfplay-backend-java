package com.pfplaybackend.api.admin.adapter.out.external;

import com.pfplaybackend.api.admin.application.port.out.AdminPartyroomPort;
import com.pfplaybackend.api.common.domain.value.UserId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.DjQueueRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomPlaybackRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackAggregationRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackReactionHistoryRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminPartyroomAdapter implements AdminPartyroomPort {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomPlaybackRepository partyroomPlaybackRepository;
    private final DjQueueRepository djQueueRepository;
    private final CrewRepository crewRepository;
    private final DjRepository djRepository;
    private final PlaybackAggregationRepository playbackAggregationRepository;
    private final PlaybackReactionHistoryRepository playbackReactionHistoryRepository;

    @Override
    public List<PartyroomData> findAllPartyrooms() {
        return partyroomRepository.findAll();
    }

    @Override
    public Optional<PartyroomData> findPartyroomById(Long id) {
        return partyroomRepository.findById(id);
    }

    @Override
    public Optional<PartyroomData> findPartyroomByLinkDomain(LinkDomain linkDomain) {
        return partyroomRepository.findByLinkDomain(linkDomain);
    }

    @Override
    public PartyroomData savePartyroom(PartyroomData partyroom) {
        return partyroomRepository.save(partyroom);
    }

    @Override
    public Optional<PartyroomPlaybackData> findPlaybackState(PartyroomId partyroomId) {
        return partyroomPlaybackRepository.findById(partyroomId);
    }

    @Override
    public PartyroomPlaybackData savePlaybackState(PartyroomPlaybackData playbackState) {
        return partyroomPlaybackRepository.save(playbackState);
    }

    @Override
    public DjQueueData saveDjQueue(DjQueueData djQueue) {
        return djQueueRepository.save(djQueue);
    }

    @Override
    public CrewData saveCrew(CrewData crew) {
        return crewRepository.save(crew);
    }

    @Override
    public Optional<CrewData> findCrewByPartyroomAndUser(PartyroomId partyroomId, UserId userId) {
        return crewRepository.findByPartyroomIdAndUserId(partyroomId, userId);
    }

    @Override
    public List<CrewData> findActiveCrewByPartyroom(PartyroomId partyroomId) {
        return crewRepository.findByPartyroomIdAndIsActiveTrue(partyroomId);
    }

    @Override
    public long countActiveCrewByPartyroom(PartyroomId partyroomId) {
        return crewRepository.countByPartyroomIdAndIsActiveTrue(partyroomId);
    }

    @Override
    public DjData saveDj(DjData dj) {
        return djRepository.save(dj);
    }

    @Override
    public boolean existsDjByPartyroomAndCrew(PartyroomId partyroomId, CrewId crewId) {
        return djRepository.existsByPartyroomIdAndCrewId(partyroomId, crewId);
    }

    @Override
    public List<DjData> findDjsByPartyroomOrderByOrder(PartyroomId partyroomId) {
        return djRepository.findByPartyroomIdOrderByOrderNumberAsc(partyroomId);
    }

    @Override
    public Optional<PlaybackAggregationData> findPlaybackAggregation(PlaybackId playbackId) {
        return playbackAggregationRepository.findById(playbackId);
    }

    @Override
    public Optional<PlaybackReactionHistoryData> findReactionHistory(PlaybackId playbackId, UserId userId) {
        return playbackReactionHistoryRepository.findByPlaybackIdAndUserId(playbackId, userId);
    }

    @Override
    public PlaybackReactionHistoryData saveReactionHistory(PlaybackReactionHistoryData history) {
        return playbackReactionHistoryRepository.save(history);
    }
}
