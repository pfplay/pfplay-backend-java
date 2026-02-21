package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.party.application.dto.result.PenaltyResult;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewPenaltyHistoryData;
import com.pfplaybackend.api.party.domain.port.PartyroomAggregatePort;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewPenaltyHistoryRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrewPenaltyQueryService {

    private final CrewPenaltyHistoryRepository crewPenaltyHistoryRepository;
    private final PartyroomAggregatePort aggregatePort;
    private final UserProfileQueryPort userProfileQueryPort;

    public List<PenaltyResult> getPenalties(PartyroomId partyroomId) {
        List<CrewPenaltyHistoryData> crewPenaltyHistoryDataList = crewPenaltyHistoryRepository.findAllByPartyroomIdAndReleasedIsFalse(partyroomId);

        List<Long> crewIds = crewPenaltyHistoryDataList.stream()
                .map(history -> history.getPunishedCrewId().getId())
                .distinct()
                .toList();
        Map<Long, CrewData> crewMap = aggregatePort.findCrewsByIds(crewIds).stream()
                .collect(Collectors.toMap(CrewData::getId, Function.identity()));

        List<UserId> punishedUserIds = crewPenaltyHistoryDataList.stream()
                .map(history -> crewMap.get(history.getPunishedCrewId().getId()).getUserId())
                .toList();
        Map<UserId, ProfileSettingDto> profileMap = userProfileQueryPort.getUsersProfileSetting(punishedUserIds);

        return crewPenaltyHistoryDataList.stream().map(history -> {
            CrewData crew = crewMap.get(history.getPunishedCrewId().getId());
            return PenaltyResult.from(history, profileMap.get(crew.getUserId()));
        }).toList();
    }
}
