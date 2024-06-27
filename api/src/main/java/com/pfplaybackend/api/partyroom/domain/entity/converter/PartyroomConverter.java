package com.pfplaybackend.api.partyroom.domain.entity.converter;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partymember;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyroomConverter {

    private final PartymemberConverter partymemberConverter;
    private final DjConverter djConverter;

    public Partyroom toDomain(PartyroomData partyroomData) {
        return new Partyroom();
//        Partyroom partyroom = Partyroom.builder()
//                .partyroomId(partyroomData.getPartyroomId())
//                .hostId(partyroomData.getHostId())
//                .isPlaybackActivated(partyroomData.isPlaybackActivated())
//                .build();
//
//        partyroom.getPartymembers().clear();
//        partyroom.getPartymembers().addAll(partyroomData.getPartymembers().stream()
//                .map(partymemberData -> {
//                    Partymember partymember = partymemberConverter.toDomain(partymemberData);
//                    partymember.setPartyroomId(partyroomData.getPartyroomId());
//                    return partymember;
//                }).toList());
//        return partyroom;
    }

    public PartyroomData toData(Partyroom partyroom) {
        return new PartyroomData();
//        Long id = partyroom.getPartyroomId() == null ? null : partyroom.getPartyroomId().getId();
//        PartyroomData partyroomData = PartyroomData.builder()
//                .id(id)
//                .hostId(partyroom.getHostId())
//                .noticeContent(partyroom.getNoticeContent())
//                .isQueueClosed(partyroom.isQueueClosed())
//                .isPlaybackActivated(partyroom.isPlaybackActivated())
//                .isTerminated(partyroom.isTerminated())
//                .build();
//
//        // Replace Partymembers Collection
//        List<PartymemberData> partymembers = partyroom.getPartymembers().stream()
//                .map(partymember -> partymemberConverter.toData(partymember, partyroomData))
//                .toList();
//        partyroomData.getPartymembers().clear();
//        partyroomData.getPartymembers().addAll(partymembers);
////        partyroomData.getPartymembers().addAll(partyroom.getPartymembers().stream()
////                .map(partymember -> partymemberConverter.toData(partymember, partyroomData)).toList());
//
//        // return new PartyroomData(partyroom.getHostId());
//        return partyroomData;
    }
}
