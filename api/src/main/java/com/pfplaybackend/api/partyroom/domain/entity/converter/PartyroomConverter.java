package com.pfplaybackend.api.partyroom.domain.entity.converter;

import com.pfplaybackend.api.partyroom.domain.entity.data.DjData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Dj;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyroomConverter {

    private final PartymemberConverter partymemberConverter;
    private final DjConverter djConverter;

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
        List<Partymember> partymembers = partyroomData.getPartymemberDataList().stream()
                .map(partymemberConverter::toDomain)
                .map(partymember -> partymember.assignPartyroomId(partyroom.getPartyroomId()))
                .toList();
        // DjData to Dj
        List<Dj> djs = partyroomData.getDjDataList().stream()
                .map(djConverter::toDomain)
                .map(dj -> dj.assignPartyroomId(partyroom.getPartyroomId()))
                .toList();

        return partyroom
                .assignPartymembers(partymembers)
                .assignDjs(djs);
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

        // Partymember to PartymemberData
        List<PartymemberData> partymemberDataList = partyroom.getPartymembers().stream()
                .map(partymemberConverter::toData)
                .map(partymemberData -> partymemberData.assignPartyroomData(partyroomData))
                .toList();
        // Dj to DjData
        List<DjData> djDataList = partyroom.getDjs().stream()
                .map(djConverter::toData)
                .map(djData -> djData.assignPartyroomData(partyroomData))
                .toList();

        return partyroomData
                .assignPartymemberListData(partymemberDataList)
                .assignDjListData(djDataList);
    }
}
