package com.pfplaybackend.api.party.application.service;

import com.pfplaybackend.api.common.ThreadLocalContext;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.party.application.aspect.context.PartyContext;
import com.pfplaybackend.api.party.application.dto.base.PartyroomDataDto;
import com.pfplaybackend.api.party.application.dto.partyroom.ActivePartyroomWithCrewDto;
import com.pfplaybackend.api.party.application.dto.result.BlockedCrewResult;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.history.CrewBlockHistoryData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.domain.exception.BlockException;
import com.pfplaybackend.api.party.domain.exception.CrewException;
import com.pfplaybackend.api.party.domain.exception.PartyroomException;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.infrastructure.repository.CrewBlockHistoryRepository;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.AddBlockRequest;
import com.pfplaybackend.api.user.application.dto.shared.ProfileSettingDto;
import com.pfplaybackend.api.user.application.service.UserProfileService;
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
    private final CrewBlockHistoryRepository blockHistoryRepository;
    private final UserProfileService userProfileService;
    private final CrewBlockHistoryRepository crewBlockHistoryRepository;
    private final PartyroomConverter partyroomConverter;

    public List<BlockedCrewResult> getBlocks() {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        ActivePartyroomWithCrewDto dto = partyroomRepository.getMyActivePartyroomWithCrewIdByUserId(partyContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));

        List<CrewBlockHistoryData> historyDataList = blockHistoryRepository.findAllByBlockerCrewIdAndUnblockedIsFalse(new CrewId(dto.getCrewId()));
        Map<UserId, ProfileSettingDto> map = userProfileService.getUsersProfileSetting(historyDataList.stream().map(CrewBlockHistoryData::getBlockedUserId).toList());

        return historyDataList.stream().map(historyData -> BlockedCrewResult.from(historyData.getId(), historyData.getBlockedCrewId(), map.get(historyData.getBlockedUserId()))).toList();
    }

    @Transactional
    public void addBlock(AddBlockRequest request) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        ActivePartyroomWithCrewDto dto = partyroomRepository.getMyActivePartyroomWithCrewIdByUserId(partyContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));


        // 차단할 때는 '같은 파티룸'에 위치해야 함을 전제로 한다.
        Optional<PartyroomDataDto> optional = partyroomRepository.findPartyroomDto(PartyroomId.of(dto.getId()));
        if(optional.isEmpty()) throw ExceptionCreator.create(PartyroomException.NOT_FOUND_ROOM);
        PartyroomDataDto partyroomDataDto = optional.get();
        PartyroomData partyroomData = partyroomConverter.toEntity(partyroomDataDto);
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);
        
        CrewId blockerCrewId = new CrewId(dto.getCrewId());
        System.out.println(blockerCrewId);
        CrewId blockedCrewId = new CrewId(request.getCrewId());
        // TODO 기존 차단 내역 조회
        Optional<CrewBlockHistoryData> historyDataOptional = blockHistoryRepository.findByBlockerCrewIdAndBlockedCrewIdAndUnblockedIsFalse(blockerCrewId, blockedCrewId);
        if(historyDataOptional.isPresent()) throw ExceptionCreator.create(BlockException.ALREADY_BLOCKED_CREW);

        CrewBlockHistoryData historyData = CrewBlockHistoryData.builder()
                .blockerCrewId(blockerCrewId)
                .blockedCrewId(blockedCrewId)
                .blockedUserId(partyroom.getCrew(blockedCrewId).getUserId())
                .blockDate(LocalDateTime.now())
                .build();

        crewBlockHistoryRepository.save(historyData);
    }

    @Transactional
    public void removeBlock(Long blockId) {
        PartyContext partyContext = (PartyContext) ThreadLocalContext.getContext();
        ActivePartyroomWithCrewDto dto = partyroomRepository.getMyActivePartyroomWithCrewIdByUserId(partyContext.getUserId())
                .orElseThrow(() -> ExceptionCreator.create(CrewException.NOT_FOUND_ACTIVE_ROOM));

        CrewId blockerCrewId = new CrewId(dto.getCrewId());
        CrewBlockHistoryData historyData  = blockHistoryRepository.findByIdAndBlockerCrewIdAndUnblockedIsFalse(blockId, blockerCrewId)
                .orElseThrow(() -> ExceptionCreator.create(BlockException.BLOCK_HISTORY_NOT_FOUND));

        historyData.setUnblocked(true);
        historyData.setUnblockDate(LocalDateTime.now());

        crewBlockHistoryRepository.save(historyData);
    }
}
