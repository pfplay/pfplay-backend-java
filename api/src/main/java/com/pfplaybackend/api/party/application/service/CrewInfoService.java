package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.result.CrewProfileSummaryResult;
import com.pfplaybackend.api.party.application.port.out.UserProfileQueryPort;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewInfoService {
    private final CrewRepository crewRepository;
    private final PartyroomInfoService partyroomInfoService;
    private final UserProfileQueryPort userProfileService;

    public CrewProfileSummaryResult getProfileSummaryByCrewId(Long crewId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        ActivePartyroomWithCrewDto activePartyroomDto = partyroomInfoService.getMyActivePartyroomWithCrewId(authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));

        CrewData crew = crewRepository.findById(crewId)
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));
        UserId targetUserId = crew.getUserId();
        AuthorityTier authorityTier = crew.getAuthorityTier();

        ProfileSummaryDto profileSummaryDto = userProfileService.getOtherProfileSummary(targetUserId, authorityTier);
        return CrewProfileSummaryResult.from(crewId, profileSummaryDto);
    }
}
