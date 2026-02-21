package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomDto;
import com.pfplaybackend.api.party.application.dto.result.BlockedCrewResult;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewBlockHistoryData;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewBlockHistoryRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.service.UserProfileQueryService;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CrewBlockQueryService {

    private final CrewBlockHistoryRepository blockHistoryRepository;
    private final UserProfileQueryService userProfileQueryService;
    private final PartyroomQueryService partyroomQueryService;

    public List<BlockedCrewResult> getBlocks() {
        AuthContext authContext = ThreadLocalContext.getAuthContext();
        ActivePartyroomDto dto = partyroomQueryService.getMyActivePartyroomOrThrow(authContext.getUserId());

        List<CrewBlockHistoryData> historyDataList = blockHistoryRepository.findAllByBlockerCrewIdAndUnblockedIsFalse(new CrewId(dto.crewId()));
        Map<UserId, ProfileSettingDto> map = userProfileQueryService.getUsersProfileSetting(historyDataList.stream().map(CrewBlockHistoryData::getBlockedUserId).toList());

        return historyDataList.stream().map(historyData -> BlockedCrewResult.from(historyData.getId(), historyData.getBlockedCrewId(), map.get(historyData.getBlockedUserId()))).toList();
    }
}
