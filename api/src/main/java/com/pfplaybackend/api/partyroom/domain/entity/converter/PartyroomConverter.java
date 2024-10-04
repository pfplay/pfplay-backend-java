package com.pfplaybackend.api.partyroom.domain.entity.converter;

import com.pfplaybackend.api.partyroom.application.dto.base.CrewDataDto;
import com.pfplaybackend.api.partyroom.application.dto.base.DjDataDto;
import com.pfplaybackend.api.partyroom.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.partyroom.application.dto.partyroom.PartyroomDto;
import com.pfplaybackend.api.partyroom.domain.entity.data.DjData;
import com.pfplaybackend.api.partyroom.domain.entity.data.CrewData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyroomConverter {

    private final CrewConverter crewConverter;
    private final DjConverter djConverter;

    public PartyroomData toEntity(PartyroomDataDto partyroomDataDto) {
        PartyroomData partyroomData = PartyroomData.from(partyroomDataDto);
        Set<CrewData> crewDataSet = partyroomDataDto.getCrewDataSet().stream()
                .map(crewDataDto -> crewDataDto.toData().assignPartyroomData(partyroomData)).collect(Collectors.toSet());
        Set<DjData> djDataSet = partyroomDataDto.getDjDataSet().stream()
                .map(djDataDto -> djDataDto.toData().assignPartyroomData(partyroomData)).collect(Collectors.toSet());

        partyroomData.assignCrewDataSet(crewDataSet);
        partyroomData.assignDjDataSet(djDataSet);

        return partyroomData;
    }

    public Partyroom toDomain(PartyroomData partyroomData) {
        Partyroom partyroom = Partyroom.builder()
                .partyroomId(partyroomData.getPartyroomId())
                .stageType(partyroomData.getStageType())
                .hostId(partyroomData.getHostId())
                .title(partyroomData.getTitle())
                .introduction(partyroomData.getIntroduction())
                .linkDomain(partyroomData.getLinkDomain())
                .playbackTimeLimit(partyroomData.getPlaybackTimeLimit())
                .noticeContent(partyroomData.getNoticeContent())
                .isPlaybackActivated(partyroomData.isPlaybackActivated())
                .currentPlaybackId(partyroomData.getCurrentPlaybackId())
                .isQueueClosed(partyroomData.isQueueClosed())
                .isTerminated(partyroomData.isTerminated())
                .build();

        // PartymemberData to Partymember
        Set<Crew> crewSet = partyroomData.getCrewDataSet().stream()
                .map(crewConverter::toDomain)
                .map(partymember -> partymember.assignPartyroomId(partyroom.getPartyroomId()))
                .collect(Collectors.toSet());
        // DjData to Dj
        Set<Dj> djSet = partyroomData.getDjDataSet().stream()
                .map(djConverter::toDomain)
                .map(dj -> dj.assignPartyroomId(partyroom.getPartyroomId()))
                .collect(Collectors.toSet());

        return partyroom
                .assignCrewSet(crewSet)
                .assignDjSet(djSet);
    }

    public PartyroomData toData(Partyroom partyroom) {
        PartyroomData partyroomData = PartyroomData.builder()
                .id(partyroom.getPartyroomId() == null ? null : partyroom.getPartyroomId().getId())
                .partyroomId(partyroom.getPartyroomId())
                .stageType(partyroom.getStageType())
                .hostId(partyroom.getHostId())
                .title(partyroom.getTitle())
                .introduction(partyroom.getIntroduction())
                .linkDomain(partyroom.getLinkDomain())
                .playbackTimeLimit(partyroom.getPlaybackTimeLimit())
                .currentPlaybackId(partyroom.getCurrentPlaybackId())
                .isPlaybackActivated(partyroom.isPlaybackActivated())
                .isQueueClosed(partyroom.isQueueClosed())
                .isTerminated(partyroom.isTerminated())
                .build();

        // Crew to CrewData
        Set<CrewData> crewDataSet = partyroom.getCrewSet().stream()
                .map(crewConverter::toData)
                .map(crewData -> crewData.assignPartyroomData(partyroomData))
                .collect(Collectors.toSet());
        // Dj to DjData
        Set<DjData> djDataSet = partyroom.getDjSet().stream()
                .map(djConverter::toData)
                .map(djData -> djData.assignPartyroomData(partyroomData))
                .collect(Collectors.toSet());

        return partyroomData
                .assignCrewDataSet(crewDataSet)
                .assignDjDataSet(djDataSet);
    }
}
