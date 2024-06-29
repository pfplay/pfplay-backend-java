package com.pfplaybackend.api.partyroom.application.service;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartymemberData;
import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.partyroom.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.partyroom.domain.value.PartyroomId;
import com.pfplaybackend.api.partyroom.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.service.UserAvatarService;
import com.pfplaybackend.api.user.application.service.UserProfileService;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyroomInfoService {

    private final PartyroomRepository partyroomRepository;
    // TODO
    private final UserProfileService userProfileService;

    public void getAllPartyrooms() {
        // TODO QueryDSL
    }

    public void getSummaryInfo(PartyroomId partyroomId) {
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId.getId()).orElseThrow();
        System.out.println(partyroomData);

        // 파티원의 UserId 추출
        List<UserId> partymemberIds = partyroomData.getPartymemberDataList().stream().map(PartymemberData::getUserId).toList();
        List<ProfileSettingDto> profileSettings = userProfileService.getUsersProfileSetting(partymemberIds);
        System.out.println(profileSettings);
    }

    public void getPartymembers(PartyroomId partyroomId) {
        //
    }

    public void getDjQueueInfo(PartyroomId partyroomId) {
        //
    }
}
