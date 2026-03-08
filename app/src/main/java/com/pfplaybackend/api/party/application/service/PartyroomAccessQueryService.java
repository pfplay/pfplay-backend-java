package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.application.dto.partyroom.LinkEnterDto;
import com.pfplaybackend.api.party.adapter.out.persistence.PlaybackRepository;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyroomAccessQueryService {

    private final PartyroomAggregatePort aggregatePort;
    private final PlaybackRepository playbackRepository;

    @Transactional(readOnly = true)
    public LinkEnterDto getPartyroomByLink(String linkDomain) {
        PartyroomData partyroom = aggregatePort.findByLinkDomain(LinkDomain.of(linkDomain))
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

        PartyroomId partyroomId = partyroom.getPartyroomId();
        long crewCount = aggregatePort.countActiveCrews(partyroomId);

        LinkEnterDto.PlaybackSummary playbackSummary = getPlaybackSummary(partyroomId);

        return new LinkEnterDto(
                partyroom.getId(),
                partyroom.getTitle(),
                partyroom.getIntroduction(),
                playbackSummary,
                crewCount
        );
    }

    private LinkEnterDto.PlaybackSummary getPlaybackSummary(PartyroomId partyroomId) {
        PartyroomPlaybackData playbackState = aggregatePort.findPlaybackState(partyroomId);
        if (!playbackState.isActivated() || playbackState.getCurrentPlaybackId() == null) {
            return null;
        }
        return playbackRepository.findById(playbackState.getCurrentPlaybackId().getId())
                .map(pb -> new LinkEnterDto.PlaybackSummary(pb.getName(), pb.getThumbnailImage()))
                .orElse(null);
    }
}
