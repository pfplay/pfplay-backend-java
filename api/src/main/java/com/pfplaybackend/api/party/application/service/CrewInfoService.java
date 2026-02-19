package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.result.CrewProfileSummaryResult;
import com.pfplaybackend.api.party.application.peer.UserProfilePeerService;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSummaryDto;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewInfoService {
    private final PartyroomRepository partyroomRepository;
    private final PartyroomInfoService partyroomInfoService;
    private final UserProfilePeerService userProfileService;

    public CrewProfileSummaryResult getProfileSummaryByCrewId(Long crewId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        ActivePartyroomWithCrewDto activePartyroomDto = partyroomInfoService.getMyActivePartyroomWithCrewId(authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));

        PartyroomId partyroomId = PartyroomId.of(activePartyroomDto.getId());
        PartyroomData partyroom = partyroomRepository.findById(partyroomId.getId())
                .orElseThrow(() -> ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM));

        CrewData crew = partyroom.getCrew(new CrewId(crewId));
        UserId targetUserId = crew.getUserId();
        AuthorityTier authorityTier = crew.getAuthorityTier();

        ProfileSummaryDto profileSummaryDto = userProfileService.getOtherProfileSummary(targetUserId, authorityTier);
        return CrewProfileSummaryResult.from(crewId, profileSummaryDto);
    }
}
