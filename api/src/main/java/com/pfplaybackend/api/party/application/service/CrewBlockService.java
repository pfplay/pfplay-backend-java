package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.common.aspect.context.AuthContext;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.result.BlockedCrewResult;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewBlockHistoryData;
import com.pfplaybackend.api.party.domain.exception.BlockException;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.infrastructure.repository.CrewBlockHistoryRepository;
import com.pfplaybackend.api.party.infrastructure.repository.CrewRepository;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.AddBlockRequest;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.profile.application.service.UserProfileService;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrewBlockService {

    private final PartyroomRepository partyroomRepository;
    private final CrewRepository crewRepository;
    private final CrewBlockHistoryRepository blockHistoryRepository;
    private final UserProfileService userProfileService;
    private final CrewBlockHistoryRepository crewBlockHistoryRepository;

    public List<BlockedCrewResult> getBlocks() {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        ActivePartyroomWithCrewDto dto = partyroomRepository.getMyActivePartyroomWithCrewIdByUserId(authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));

        List<CrewBlockHistoryData> historyDataList = blockHistoryRepository.findAllByBlockerCrewIdAndUnblockedIsFalse(new CrewId(dto.getCrewId()));
        Map<UserId, ProfileSettingDto> map = userProfileService.getUsersProfileSetting(historyDataList.stream().map(CrewBlockHistoryData::getBlockedUserId).toList());

        return historyDataList.stream().map(historyData -> BlockedCrewResult.from(historyData.getId(), historyData.getBlockedCrewId(), map.get(historyData.getBlockedUserId()))).toList();
    }

    @Transactional
    public void addBlock(AddBlockRequest request) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        ActivePartyroomWithCrewDto dto = partyroomRepository.getMyActivePartyroomWithCrewIdByUserId(authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));

        CrewId blockerCrewId = new CrewId(dto.getCrewId());
        CrewId blockedCrewId = new CrewId(request.getCrewId());
        Optional<CrewBlockHistoryData> historyDataOptional = blockHistoryRepository.findByBlockerCrewIdAndBlockedCrewIdAndUnblockedIsFalse(blockerCrewId, blockedCrewId);
        if(historyDataOptional.isPresent()) throw ExceptionCreator.create(BlockException.ALREADY_BLOCKED_CREW);

        CrewData blockedCrew = crewRepository.findById(blockedCrewId.getId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));

        CrewBlockHistoryData historyData = CrewBlockHistoryData.builder()
                .blockerCrewId(blockerCrewId)
                .blockedCrewId(blockedCrewId)
                .blockedUserId(blockedCrew.getUserId())
                .blockDate(LocalDateTime.now())
                .unblocked(false)
                .build();

        crewBlockHistoryRepository.save(historyData);
    }

    @Transactional
    public void removeBlock(Long blockId) {
        AuthContext authContext = (AuthContext) ThreadLocalContext.getContext();
        ActivePartyroomWithCrewDto dto = partyroomRepository.getMyActivePartyroomWithCrewIdByUserId(authContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));

        CrewId blockerCrewId = new CrewId(dto.getCrewId());
        CrewBlockHistoryData historyData  = blockHistoryRepository.findByIdAndBlockerCrewIdAndUnblockedIsFalse(blockId, blockerCrewId)
                .orElseThrow(() -> ExceptionCreator.create(BlockException.BLOCK_HISTORY_NOT_FOUND));

        historyData.setUnblocked(true);
        historyData.setUnblockDate(LocalDateTime.now());

        crewBlockHistoryRepository.save(historyData);
    }
}
